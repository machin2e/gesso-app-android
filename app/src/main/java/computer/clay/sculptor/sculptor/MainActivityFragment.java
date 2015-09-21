package computer.clay.sculptor.sculptor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> httpRequestAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Define the data
        ArrayList<String> httpRequests = new ArrayList<String>();
        httpRequests.add("GET /channels");
        httpRequests.add("POST /channel/1");
        httpRequests.add("GET /experience"); // i.e., this is rather than the memory, store, or database
        httpRequests.add("GET /behavior");

        // Define the adapter (adapts the data to the actual rendered view)
        httpRequestAdapter = new ArrayAdapter<String>( // ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this fragment's parent activity).
                R.layout.list_item_http_request, // ID of list item layout
                R.id.list_item_http_request_textview, // ID of textview to populate (using the specified list item layout)
                httpRequests // The list of forecast data
        );

        // Define the view (get a reference to it and pass it an adapter)
        ListView listView = (ListView) rootView.findViewById(R.id.listview_http_requests);
        listView.setAdapter(httpRequestAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

//                Context context = view.getContext();
                String httpRequestText = httpRequestAdapter.getItem(position); //CharSequence text = "Hello toast!";
//                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), httpRequestText, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
                toast.show();

//                // Executed in an Activity, so 'this' is the Context
//                // The fileUrl is a string URL, such as "http://www.example.com/image.png"
//                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT, forecast);
//                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: Handle the selected options item.

        return super.onOptionsItemSelected(item);
    }
}
