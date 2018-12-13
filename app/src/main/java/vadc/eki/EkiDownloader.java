package vadc.eki;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EkiDownloader {

    public String search(String param) {
        String result = "Ei leitud";
        try {
            //load first page
            Document firstPage = Jsoup.connect("http://eki.ee/dict/" + MainActivity.SELECTED_SOURCE + "/index.cgi?Q=" + param).get();
            Elements basicInfo = firstPage.getElementsByClass("tervikart");
            basicInfo.get(0).getElementsByClass("kuulanaidet").remove();
            basicInfo.get(0).getElementsByTag("audio").remove();
            basicInfo.get(0).getElementsByClass("teisedallikad").remove();

            if (MainActivity.SELECTED_SOURCE.equals("psv")) {
                //example ID c2123_1_m
                String id = firstPage.getElementsByClass("leitud_ss").parents().get(0).attr("id");
                id = id.substring(1, id.indexOf('_'));
                //load forms table
                Document tableDocument = Jsoup.connect("http://eki.ee/dict/psv/vormid4.cgi?A=" + id + "_1").get();
                tableDocument.prepend(basicInfo.toString());
                result = tableDocument.toString();
            } else {
                result = basicInfo.toString();
            }
        } catch (Exception e) {
            //noop
        }
        return result;
    }
}
