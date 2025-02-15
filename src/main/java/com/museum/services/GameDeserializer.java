package com.museum.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.museum.data.Artwork;
import com.museum.data.Game;
import com.museum.data.Guard;
import com.museum.data.Museum;
import com.museum.data.Player;
import com.museum.data.Room;
import com.museum.enumerator.ArtworkType;
import com.museum.enumerator.DirectionType;
import com.museum.enumerator.StealingToolType;

public class GameDeserializer extends JsonDeserializer<Game> {

    /*
     * metodo che effettua la deserializzazione di game.
     * chiamato automaticamente quando leggo il file
     */
    @Override
    public Game deserialize(JsonParser parser, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        Player player = null;
        Guard guard = null;
        int currentProfit = 0;
        int timeRemaining = 0;

        /*
         * Questa prima parte solo nel caso in cui non sto deserializzando la
         * configurazione iniziale
         */

        if (parser.currentToken() != JsonToken.START_ARRAY) {
            int x_coord;
            int y_coord;
            StealingToolType stealingToolInventory;
            List<Artwork> artworkInventory = new ArrayList<>();

            /*
             * Inizio oggetto player
             */

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            x_coord = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            y_coord = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            String tmp = parser.getValueAsString();
            if (tmp != null) {
                stealingToolInventory = StealingToolType.valueOf(tmp.toUpperCase());
            } else {
                stealingToolInventory = null;
            }

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            if (parser.currentToken() != JsonToken.END_ARRAY) {
                String artwName;
                int value;
                int weight;
                ArtworkType type;

                while (parser.currentToken() != JsonToken.END_ARRAY) {

                    parser.nextToken();
                    parser.nextToken();

                    type = ArtworkType.valueOf(parser.getValueAsString().toUpperCase());

                    parser.nextToken();
                    parser.nextToken();

                    artwName = parser.getValueAsString();

                    parser.nextToken();
                    parser.nextToken();

                    value = parser.getValueAsInt();

                    parser.nextToken();
                    parser.nextToken();

                    weight = parser.getValueAsInt();

                    parser.nextToken();
                    parser.nextToken();

                    /*
                     * aggiugno una opera alla lista
                     */

                    artworkInventory.add(new Artwork(artwName, value, weight, type));
                }
            }
            player = new Player(artworkInventory, stealingToolInventory, x_coord, y_coord);

            /*
             * fine oggetto player
             */

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            /*
             * inizio oggetto guardia
             */
            x_coord = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            y_coord = parser.getValueAsInt();

            guard = new Guard(x_coord, y_coord);
            /*
             * fine oggetto guarida
             */

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            /*
             * inizio oggetto timer
             */
            timeRemaining = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            /*
             * current profit
             */

            currentProfit = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

        }

        String tmp;
        JsonToken token;
        String name;
        int x_val;
        int y_val;
        boolean isEntry;
        List<DirectionType> allowedDirections;
        List<Artwork> artworksList;
        StealingToolType stealingTool;
        List<Room> roomsList = new ArrayList<>();

        parser.nextToken();
        /*
         * arrivo nella lista delle stanze
         */
        while (parser.currentToken() == JsonToken.START_OBJECT) {
            parser.nextToken();
            parser.nextToken();

            name = parser.getValueAsString();

            parser.nextToken();
            parser.nextToken();

            x_val = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            y_val = parser.getValueAsInt();

            parser.nextToken();
            parser.nextToken();

            isEntry = parser.getValueAsBoolean();

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            allowedDirections = new ArrayList<>();

            /*
             * arrivo alla lista delle direzioni consentite
             */

            while (parser.currentToken() != JsonToken.END_ARRAY) {
                allowedDirections.add(DirectionType.valueOf(parser.getValueAsString().toUpperCase()));
                parser.nextToken();
            }

            parser.nextToken();
            parser.nextToken();
            parser.nextToken();

            /*
             * arrivo alla lista delle opere, se presenti
             */

            if (parser.currentToken() != JsonToken.END_ARRAY) {
                String artwName;
                int value;
                int weight;
                ArtworkType type;
                artworksList = new ArrayList<>();

                while (parser.currentToken() != JsonToken.END_ARRAY) {

                    parser.nextToken();
                    parser.nextToken();

                    type = ArtworkType.valueOf(parser.getValueAsString().toUpperCase());

                    parser.nextToken();
                    parser.nextToken();

                    artwName = parser.getValueAsString();

                    parser.nextToken();
                    parser.nextToken();

                    value = parser.getValueAsInt();

                    parser.nextToken();
                    parser.nextToken();

                    weight = parser.getValueAsInt();

                    parser.nextToken();
                    parser.nextToken();

                    /*
                     * aggiugno una opera alla lista
                     */

                    artworksList.add(new Artwork(artwName, value, weight, type));
                }
            } else {
                artworksList = new ArrayList<>();
            }

            parser.nextToken();
            parser.nextToken();

            tmp = parser.getValueAsString();
            if (tmp != null) {
                stealingTool = StealingToolType.valueOf(tmp.toUpperCase());
            } else {
                stealingTool = null;
            }

            /*
             * aggiungo una stanza alla lista
             */

            roomsList.add(new Room(name, x_val, y_val, isEntry, allowedDirections, stealingTool, artworksList));

            parser.nextToken();
            parser.nextToken();
        }

        if (player == null) {
            return new Game(roomsList);
        } else {
            return new Game(player, new Museum(roomsList), guard, currentProfit, timeRemaining);
        }
    }

}
