package kz.asoft.import_stat_gov_kz_sole_trader;

import java.io.IOException;
import java.net.*;
import org.json.*;

public class FileDownloader {

    public void getFile(int typeLegalUnitId) {

        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.1.20.15", 8888));

            // Получение списка срезов
            String jsonString = new HttpUtility().get("https://stat.gov.kz/api/rcut/ru", proxy);
            if (jsonString == null) {
                System.out.println("Ошибка! Не удалось получить список срезов");
                return;
            }

            // Находим идентификатор последнего среза
            int lastCutId = -1;
            JSONArray ja = new JSONArray(jsonString.toString());
            for (int i = 0; i < ja.length(); i++) {
                int id = ja.getJSONObject(i).getInt("id");
                if (id > lastCutId) {
                    lastCutId = id;
                }
            }

            // Отправление запроса на выборку и получение номера заявки
            String input = "{\"conditions\":[" +
                    "        {\"classVersionId\":2153,\"itemIds\":[" + typeLegalUnitId + "]}," +
                            "{\"classVersionId\":1989,\"itemIds\":[39354,39355,39356,39358,534829,39359]}" +
                            "]," +
                            "\"cutId\":" + lastCutId + ",\"stringForMD5\":\"string\"}";
            String jsonString2 = new HttpUtility().post("https://stat.gov.kz/api/sbr/request/?api", input, proxy);
            JSONObject jo2 = new JSONObject(jsonString2);
            if (!jo2.getBoolean("success")) {
                System.out.println("Ошибка! Запрос на выборку не обработан");
                return;
            }
            String objNumber = jo2.getString("obj");

            //  Проверка статуса заявки
            String jsonString3 = "";
            JSONObject jo3 = null;
            String fileGuid = "";
            for (int i = 1; i <= 5; i++) {
                jsonString3 = new HttpUtility().get("https://stat.gov.kz/api/sbr/requestResult/" + objNumber + "/ru", proxy);
                jo3 = new JSONObject(jsonString3);

                if (!jo3.getBoolean("success")) {
                    System.out.println("Ошибка! Не возможно проверить статус заявки");
                    return;
                }

                if (jo3.getString("description").equals("Обработан")) {
                    fileGuid = jo3.getJSONObject("obj").getString("fileGuid");
                    break;
                }

                Thread.sleep(60000);
            }
            if (fileGuid.isEmpty()) {
                System.out.println("Ошибка! Время выделенное на проверку статуса заявки окончено");
                return;
            }
            System.out.println("fileGuid " + fileGuid);

            // Загрузка zip-архива среза
            new HttpUtility().downloadFile("https://stat.gov.kz/api/sbr/download?bucket=SBR_UREQUEST&guid=" + fileGuid, "c:\\windows\\temp", proxy);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }
}
