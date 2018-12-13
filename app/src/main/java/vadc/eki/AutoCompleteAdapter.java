package vadc.eki;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> searchData;

    public AutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        searchData = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return searchData.size();
    }

    @Override
    public String getItem(int index) {
        return searchData.get(index);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                searchData = new ArrayList<>();
                FilterResults filterResults = new FilterResults();
                if(constraint != null) {
                    try {
                        Document elements = Jsoup.connect("http://eki.ee/dict/suggest.cgi?D=" + MainActivity.SELECTED_SOURCE + "&F=M&term=" + constraint).get();
                        Elements suggestions = elements.body().getElementsByTag("span");
                        for (Element suggestion : suggestions) {
                            searchData.add(suggestion.ownText());
                        }
                    }
                    catch(Exception e) {
                        Log.e("exception", e.getMessage());
                    }
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}