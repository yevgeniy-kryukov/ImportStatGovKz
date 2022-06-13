package kz.asoft.import_stat_gov_kz_sole_trader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.*;
import java.io.OutputStream;

public class HttpUtility {
    public String get(String strURL, Proxy proxy) {
        try {
            // Получение списка срезов
            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
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

            return jsonString.toString();
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }

    public String post(String strURL, String strParam, Proxy proxy) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(strParam.getBytes());
            os.flush();

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output = "";
            StringBuilder jsonString = new StringBuilder();
            while ((output = br.readLine()) != null) {
                jsonString.append(output);
            }

            conn.disconnect();

            return jsonString.toString();
        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }
}
