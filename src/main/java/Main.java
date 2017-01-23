import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by Asus Laptop on 22-Jan-17.
 */
public class Main {

    /**
     * EX 1:
     * <p>
     * Comunicar com a API do Spotify e por cada Artista da lista apresentar no ecran
     * as suas Top Tracks em Portugal, apresentar:
     * nome do album, nome da musica e posição no album
     * <p>
     * <p>
     * EX 2:
     * <p>
     * Comunicar com a API do Spotify e por cada Artista da lista saber quantos albuns existem no
     * Spotify escrever na base de dados o nome do Artista e o numero total de albuns que existe e no final
     * ler da base de dados e apresentar essa informação no ecran
     */

    /**
     * NOTAS
     * <p>
     * Cada exercicio deve ser implementado em branch de git próprio e no final
     * de cada exercicio deve ser feito merge com o branch master
     */

    /**
     * REFERÊNCIAS
     * <p>
     * Spotify API:
     * Procurar um Artista -> https://developer.spotify.com/web-api/search-item/
     * Top Tracks de um Artista -> https://developer.spotify.com/web-api/get-artists-top-tracks/
     * Albuns de um Artista -> https://developer.spotify.com/web-api/get-artists-albums/
     * Testes no Browser -> https://developer.spotify.com/web-api/console/
     * <p>
     * <p>
     * Unirest:
     * http://unirest.io/java.html
     */

    public static List<String> artists = Arrays.asList("Led Zeppelin",
            "Pink Floyd", "Rolling Stones",
            "Garbage", "Porcupine Tree",
            "Nirvana", "Pearl Jam");

    public static void main(String args[]) throws UnirestException, SQLException, IOException, ClassNotFoundException {


        Connection conn = getConnection();



        for(int i=0;i<artists.size();i++) {

            HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.spotify.com/v1/search")
                    .header("accept", "application/json")
                    .queryString("q", artists.get(i))
                    .queryString("type","artist")
                    .asJson();

            String valorID = ((JSONObject)
                    ((JSONArray)
                            ((JSONObject) jsonResponse.getBody().getObject().get("artists")).get("items")).get(0)).getString("id");

            jsonResponse = Unirest.get("https://api.spotify.com/v1/artists/"+valorID+"/albums")
                    .header("accept", "application/json")
                    .queryString("q", artists.get(i))
                    .queryString("album_type", "album")
                    .asJson();

            System.out.print("nome artista: "+artists.get(i)+"\t");
            int n_albuns = (Integer) jsonResponse.getBody().getObject().get("total");


            Statement statement = conn.createStatement();

            ResultSet dados = statement.executeQuery("SELECT * FROM artistas");

            while (dados.next()) {
                String nomebd = dados.getString("nome");
                int numbd = dados.getInt("albuns");

                System.out.println(nomebd + "\t" + numbd);
            }

            statement.close();

            //statement.executeUpdate("INSERT INTO artistas (nome, albuns)" + "VALUES ('"+artists.get(i)+"', '"+n_albuns+"') ");

            System.out.println(" ");




        }

        conn.close();

    }



/*
HttpResponse<JsonNode> jsonResponse = Unirest.post("http://httpbin.org/post")
  .header("accept", "application/json")
  .queryString("apiKey", "123")
  .field("parameter", "value")
  .field("foo", "bar")
  .asJson();

 */


    private static Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        /**
         * Existe uma Tabela chamada "artistas" com o seguinte formato
         *
         * "nome" - VARCHAR(255)
         * "albuns" - INT
         *
         */
        InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties");
        Properties props = new Properties();
        props.load(input);
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection("jdbc:postgresql://horton.elephantsql.com/hzscixli", props);
}
}
