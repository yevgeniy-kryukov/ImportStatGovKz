package kz.asoft.import_stat_gov_kz_sole_trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.*;
import java.io.OutputStream;

public class FileDownloader {

    public void getFile(int typeLegalUnitId) {
        try {

            // Получение списка срезов
            URL url = new URL("https://stat.gov.kz/api/rcut/ru");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            String output = "";
            StringBuilder jsonString = new StringBuilder();
            while ((output = br.readLine()) != null) {
                jsonString.append(output);
            }

            conn.disconnect();

            // Находим идентификатор последнего среза
            int lastCutId = -1;
            JSONArray ja = new JSONArray(jsonString.toString());
            for (int i = 0; i < ja.length(); i++)
            {
                int id = ja.getJSONObject(i).getInt("id");
                if (id > lastCutId) {
                    lastCutId = id;
                }
            }
            //System.out.println(lastCutId);

            // Отправление запроса на выборку и получение номера заявки
            url = new URL("https://stat.gov.kz/api/sbr/request/?api");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"conditions\":[" +
                    "        {\"classVersionId\":2153,\"itemIds\":[" + typeLegalUnitId + "]}," +
                            "{\"classVersionId\":1989,\"itemIds\":[39354,39355,39356,39358,534829,39359]}" +
                            "]," +
                            "\"cutId\":" + lastCutId + ",\"stringForMD5\":\"string\"}";
            System.out.println(input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

//            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }

            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            output = "";
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();



        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }
}
