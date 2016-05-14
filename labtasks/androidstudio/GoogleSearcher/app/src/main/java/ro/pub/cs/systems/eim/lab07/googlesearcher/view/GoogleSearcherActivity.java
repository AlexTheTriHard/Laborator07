package ro.pub.cs.systems.eim.lab07.googlesearcher.view;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import ro.pub.cs.systems.eim.lab07.googlesearcher.R;
import ro.pub.cs.systems.eim.lab07.googlesearcher.general.Constants;

public class GoogleSearcherActivity extends AppCompatActivity {

    private EditText keywordEditText;
    private WebView googleResultsWebView;
    private Button searchGoogleButton;

    private SearchGoogleButtonClickListener searchGoogleButtonClickListener = new SearchGoogleButtonClickListener();
    private class SearchGoogleButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {

            // TODO: exercise 6a)
            // obtain the keyword from keywordEditText
            String query = keywordEditText.getText().toString();
            // signal an empty keyword through an error message displayed in a Toast window
            if (query == null || query.isEmpty()) {
                Toast.makeText(getApplication(), Constants.EMPTY_KEYWORD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
            } else {
                // split a multiple word (separated by space) keyword and link them through +
                String[] keywords = query.split(" ");
                // prepend the keyword with "search?q=" string
                query = Constants.SEARCH_PREFIX + keywords[0];
                for (int k = 1; k < keywords.length; k++) {
                    query += "+" + keywords[k];
                }
                // start the GoogleSearcherAsyncTask passing the keyword
                new GoogleSearcherAsyncTask().execute(query);
            }




        }
    }

    private class GoogleSearcherAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            // TODO: exercise 6b)
            // create an instance of a HttpClient object
            HttpClient httpClient = new DefaultHttpClient();
            // create an instance of a HttpGet object, encapsulating the base Internet address (http://www.google.com) and the keyword
            HttpGet httpGet = new HttpGet(Constants.GOOGLE_INTERNET_ADDRESS + params[0]);
            // create an instance of a ResponseHandler object
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            // execute the request, thus generating the result
            try {
                return httpClient.execute(httpGet, responseHandler);
            } catch (ClientProtocolException clientProtocolException) {
                Log.e(Constants.TAG, clientProtocolException.getMessage());
                if (Constants.DEBUG) {
                    clientProtocolException.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String content) {

            // TODO: exercise 6b)
            // display the result into the googleResultsWebView through loadDataWithBaseURL() method
            // - base Internet address is http://www.google.com
            // - page source code is the response
            // - mimetype is text/html
            // - encoding is UTF-8
            // - history is null
            googleResultsWebView.loadDataWithBaseURL(
                    Constants.GOOGLE_INTERNET_ADDRESS,
                    content,
                    Constants.MIME_TYPE,
                    Constants.CHARACTER_ENCODING,
                    null);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_searcher);

        keywordEditText = (EditText) findViewById(R.id.keyword_edit_text);
        googleResultsWebView = (WebView) findViewById(R.id.google_results_web_view);

        searchGoogleButton = (Button) findViewById(R.id.search_google_button);
        searchGoogleButton.setOnClickListener(searchGoogleButtonClickListener);
    }
}
