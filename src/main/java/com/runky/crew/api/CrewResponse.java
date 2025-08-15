package com.runky.crew.api;

import com.runky.crew.application.CrewResult;
import java.util.List;

public class CrewResponse {
    public record Create(
            Long crewId,
            String name,
            String code
    ) {
        public static Create from(CrewResult result) {
            return new Create(result.id(), result.name(), result.code());
        }
    }

    public record Join(
            Long crewId
    ) {
        public static Join from(CrewResult result) {
            return new Join(result.id());
        }
    }

    public record Cards(
            List<Card> crews
    ) {
        public static Cards from(List<CrewResult.Card> cards) {
            List<Card> crewCards = cards.stream()
                    .map(Card::from)
                    .toList();
            return new Cards(crewCards);
        }
    }

    public record Card(
            Long crewId,
            String name,
            Long memberCount,
            boolean isLeader,
            List<String> characters
    ) {
        public static Card from(CrewResult.Card card) {
            return new Card(
                    card.crewId(),
                    card.crewName(),
                    card.memberCount(),
                    card.isLeader(),
                    card.characters()
            );
        }
    }

    private CrewResponse() {
    }
}
