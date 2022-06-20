package kz.asoft.import_stat_gov_kz;

import java.net.*;
import org.json.*;

class FileDownloader {
    private final Proxy proxy;

    FileDownloader(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getFile(int cutId, int typeLegalUnitId, String listSitCodes, String downloadDir) throws Exception  {

        // Отправление запроса на выборку и получение номера заявки
        String input = "{\"conditions\":[" +
                "        {\"classVersionId\":2153,\"itemIds\":[" + typeLegalUnitId + "]}," +
                        "{\"classVersionId\":1989,\"itemIds\":[" +  listSitCodes + "]}" +
                        "]," +
                        "\"cutId\":" + cutId + ",\"stringForMD5\":\"string\"}";
        String jsonString2 = new HttpUtility(proxy).post("https://stat.gov.kz/api/sbr/request/?api", input);
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
            jsonString3 = new HttpUtility(proxy).get("https://stat.gov.kz/api/sbr/requestResult/" + objNumber + "/ru");
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
        return new HttpUtility(proxy).downloadFile("https://stat.gov.kz/api/sbr/download?bucket=SBR_UREQUEST&guid=" + fileGuid, downloadDir);
    }
}
