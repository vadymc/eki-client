package vadc.eki;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    public static String SELECTED_SOURCE = "";

    private static String lastSearchResult = "";

    private EkiDownloader ekiDownloader = new EkiDownloader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setText(lastSearchResult);
        SELECTED_SOURCE = getSelectedSource();
        ArrayAdapter<String> adapter = new AutoCompleteAdapter(this, android.R.layout.simple_dropdown_item_1line);
        final AutoCompleteTextView searchField = (AutoCompleteTextView) findViewById(R.id.searchText);
        searchField.setAdapter(adapter);
        searchField.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (searchField.getCompoundDrawables()[2] == null)
                    return false;

                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;

                if (event.getX() > searchField.getWidth() - searchField.getPaddingRight() - 80) {
                    searchField.setText("");
                }

                return false;
            }
        });
        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.this.sendSearch(view);
            }
        });
        RadioGroup sourceRadioGroup = (RadioGroup) findViewById(R.id.source);
        sourceRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                //save selected
                RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                SELECTED_SOURCE = radioButton.getText().toString();

                //clear page
                final WebView view = (WebView) findViewById(R.id.searchResult);
                view.loadUrl("about:blank");
                AutoCompleteTextView searchField = (AutoCompleteTextView) findViewById(R.id.searchText);
                searchField.setText("");
            }
        });
    }

    public void sendSearch(View view) {
        AutoCompleteTextView searchField = (AutoCompleteTextView) findViewById(R.id.searchText);
        String message = searchField.getText().toString();

        setText("Leitakse " + message);
        new AsyncEkiDownloader().execute(message);
    }

    public String getSelectedSource() {
        RadioGroup sourceRadioGroup = (RadioGroup) findViewById(R.id.source);
        int radioButtonID = sourceRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) sourceRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }

    @NonNull
    private void setText(String text) {
        final WebView view = (WebView) findViewById(R.id.searchResult);
        view.loadData(text, "text/html; charset=utf-8", "UTF-8");
    }

    private class AsyncEkiDownloader extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return ekiDownloader.search(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                MainActivity.this.lastSearchResult = result;
                MainActivity.this.setText(result);
            } catch (Exception e) {
            }
        }
    }
}
