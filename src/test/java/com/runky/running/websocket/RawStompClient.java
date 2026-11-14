package com.runky.running.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

/**
 * 소켓을 직접 다루는 STOMP over WebSocket 테스트 클라이언트.
 * 일반 WebSocket 라이브러리는 서버의 close 프레임에 자동으로 응답하기 때문에, "응답하지 않는 클라이언트"를 만들려면 프레임을 직접 다뤄야 한다.
 */
final class RawStompClient implements AutoCloseable {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final Socket socket;
	private final InputStream in;
	private final OutputStream out;

	private RawStompClient(Socket socket) throws IOException {
		this.socket = socket;
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
	}

	/** raw /ws/websocket 으로 접속해 STOMP CONNECT(heart-beat:5000,5000) 까지 마친다. */
	static RawStompClient connect(int port) throws IOException {
		Socket socket = new Socket("127.0.0.1", port);
		socket.setSoTimeout(30_000);
		RawStompClient client = new RawStompClient(socket);
		client.handshake(port);
		client.sendText("CONNECT\naccept-version:1.2\nhost:localhost\nheart-beat:5000,5000\n\n\0");
		client.readTextStartingWith("CONNECTED");
		return client;
	}

	/** 정상 종료: DISCONNECT(receipt) → RECEIPT → close(1000) */
	void disconnectNormally() throws IOException {
		sendText("DISCONNECT\nreceipt:bye\n\n\0");
		readTextStartingWith("RECEIPT");
		sendClose(1000);
	}

	void sendClose(int code) throws IOException {
		sendFrame(0x8, new byte[] {(byte)(code >> 8), (byte)code});
	}

	/** close 프레임 없이 TCP 만 끊는다 (네트워크가 끊긴 상황) */
	void dropConnection() throws IOException {
		socket.close();
	}

	/** 서버가 보낸 close 프레임을 기다려 close code 를 돌려준다. 이 클라이언트는 응답하지 않는다. */
	int awaitCloseFrameFromServer() throws IOException {
		while (true) {
			Frame frame = readFrame();
			if (frame.opcode() == 0x8) {
				return ((frame.payload()[0] & 0xFF) << 8) | (frame.payload()[1] & 0xFF);
			}
		}
	}

	/** 주어진 시간 안에 서버가 TCP 연결을 닫으면(FIN) true */
	boolean serverClosesConnectionWithin(Duration timeout) throws IOException {
		socket.setSoTimeout((int)timeout.toMillis());
		try {
			while (in.read() != -1) {
				// 남은 바이트는 버린다
			}
			return true;
		} catch (SocketTimeoutException e) {
			return false;
		}
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	private void handshake(int port) throws IOException {
		byte[] nonce = new byte[16];
		RANDOM.nextBytes(nonce);
		String request = "GET /ws/websocket HTTP/1.1\r\n"
			+ "Host: 127.0.0.1:" + port + "\r\n"
			+ "Upgrade: websocket\r\n"
			+ "Connection: Upgrade\r\n"
			+ "Sec-WebSocket-Key: " + Base64.getEncoder().encodeToString(nonce) + "\r\n"
			+ "Sec-WebSocket-Version: 13\r\n"
			+ "Sec-WebSocket-Protocol: v12.stomp\r\n\r\n";
		out.write(request.getBytes(StandardCharsets.US_ASCII));
		out.flush();

		ByteArrayOutputStream headers = new ByteArrayOutputStream();
		while (!headers.toString(StandardCharsets.US_ASCII).endsWith("\r\n\r\n")) {
			int b = in.read();
			if (b == -1) {
				throw new IOException("handshake 중 연결이 끊김");
			}
			headers.write(b);
		}
		String response = headers.toString(StandardCharsets.US_ASCII);
		if (!response.startsWith("HTTP/1.1 101")) {
			throw new IOException("WebSocket 업그레이드 실패: " + response);
		}
	}

	private void sendText(String text) throws IOException {
		sendFrame(0x1, text.getBytes(StandardCharsets.UTF_8));
	}

	/** 클라이언트 → 서버 프레임은 반드시 마스킹한다 (RFC 6455) */
	private void sendFrame(int opcode, byte[] payload) throws IOException {
		byte[] mask = new byte[4];
		RANDOM.nextBytes(mask);
		ByteArrayOutputStream frame = new ByteArrayOutputStream();
		frame.write(0x80 | opcode);
		if (payload.length < 126) {
			frame.write(0x80 | payload.length);
		} else {
			frame.write(0x80 | 126);
			frame.write(payload.length >> 8);
			frame.write(payload.length);
		}
		frame.write(mask);
		for (int i = 0; i < payload.length; i++) {
			frame.write(payload[i] ^ mask[i % 4]);
		}
		out.write(frame.toByteArray());
		out.flush();
	}

	private String readTextStartingWith(String command) throws IOException {
		while (true) {
			Frame frame = readFrame();
			String text = new String(frame.payload(), StandardCharsets.UTF_8);
			if (frame.opcode() == 0x1 && text.startsWith(command)) {
				return text;
			}
		}
	}

	private Frame readFrame() throws IOException {
		int first = readByte();
		int length = readByte() & 0x7F;
		if (length == 126) {
			length = (readByte() << 8) | readByte();
		} else if (length == 127) {
			throw new IOException("테스트에서 쓰지 않는 큰 프레임");
		}
		byte[] payload = in.readNBytes(length);
		return new Frame(first & 0x0F, payload);
	}

	private int readByte() throws IOException {
		int b = in.read();
		if (b == -1) {
			throw new IOException("서버가 연결을 닫음");
		}
		return b;
	}

	private record Frame(int opcode, byte[] payload) {
	}
}
