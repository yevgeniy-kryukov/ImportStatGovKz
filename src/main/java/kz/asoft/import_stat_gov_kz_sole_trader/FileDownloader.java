package kz.asoft.import_stat_gov_kz_sole_trader;

import java.net.*;
import org.json.*;

public class FileDownloader {

    public String getFile(int typeLegalUnitId, String downloadDir) throws Exception  {

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.1.20.15", 8888));

        // Получение списка срезов
        String jsonString = new HttpUtility().get("https://stat.gov.kz/api/rcut/ru", proxy);
        if (jsonString == null) {
            throw new Exception("Ошибка! Не удалось получить список срезов");
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
            throw new Exception("Ошибка! Запрос на выборку не обработан");
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
                throw new Exception("Ошибка! Не возможно проверить статус заявки");
            }

            if (jo3.getString("description").equals("Обработан")) {
                fileGuid = jo3.getJSONObject("obj").getString("fileGuid");
                break;
            }

            Thread.sleep(60000);
        }
        if (fileGuid.isEmpty()) {
            throw new Exception("Ошибка! Время выделенное на проверку статуса заявки окончено");
        }
        //System.out.println("fileGuid " + fileGuid);

        // Загрузка zip-архива среза
        return new HttpUtility().downloadFile("https://stat.gov.kz/api/sbr/download?bucket=SBR_UREQUEST&guid=" + fileGuid, downloadDir, proxy);
    }
}
