package kz.asoft.import_stat_gov_kz;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class HttpUtility {
    private final Proxy proxy;

    HttpUtility(Proxy proxy) {
        this.proxy = proxy;
    }

    public String get(String strURL) throws IOException  {
        try {
            // Получение списка срезов
            URL url = new URL(strURL);
            HttpURLConnection conn;
            if (proxy != null) {
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
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

    public String post(String strURL, String strParam) throws IOException  {
        try {
            URL url = new URL(strURL);
            HttpURLConnection conn;
            if (proxy != null) {
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
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

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @param proxy
     * @throws IOException
     */
    public String downloadFile(String fileURL, String saveDir) throws Exception {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn;
        if (proxy != null) {
            httpConn = (HttpURLConnection) url.openConnection(proxy);
        } else {
            httpConn = (HttpURLConnection) url.openConnection();
        }
        int responseCode = httpConn.getResponseCode();

        String fileName = "";
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }

//            System.out.println("Content-Type = " + contentType);
//            System.out.println("Content-Disposition = " + disposition);
//            System.out.println("Content-Length = " + contentLength);
//            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            final int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            //System.out.println("File downloaded");
        } else {
            throw new Exception("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        return fileName;
    }
}
