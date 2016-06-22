package dot.todo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> todoItems;
    ArrayAdapter<String> todoAdapter;
    ListView lvItems;
    EditText etEditText;

    //REQUEST_CODE for INTENT
    final int REQUEST_CODE = 200;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateListView();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //List View
        lvItems = (ListView) findViewById(R.id.lvItems);
        //Adapter
        lvItems.setAdapter(todoAdapter);
        //Input
        etEditText = (EditText) findViewById(R.id.etEditText);
        //Remove on longClick
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoItems.remove(position);
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Removed " , Toast.LENGTH_SHORT).show();
                writeItems();
                return true;
            }
        });


        //Edit Item **by sending to editActivity
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  launchEditActivity(position, view);
            }
        });


    }

    public void populateListView() {
        readItems();
        //ArrayAdapter takes (context, layout, array)
        todoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, todoItems);
    }

    public void onAddItem(View view) {
        /* TODO:
            check to see if field is empty before adding blank items to list
        */

        //Add To Array
        todoAdapter.add(etEditText.getText().toString());
        //Set Field back to blank
        etEditText.setText("");
        //Store Item to Android Memory
        writeItems();
        //Show Toast Messages
        Toast.makeText(getApplicationContext(), "Added " , Toast.LENGTH_SHORT).show();

    }

    public void readItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.text");
        try {
            todoItems = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e) {
            Log.i("Read Item ", e.getMessage());
        }
    }

    public void writeItems() {
        File filesDir = getFilesDir();
        File file = new File(filesDir, "todo.text");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException e) {
            Log.i("Add Item ", e.getMessage());
        }
    }



    //EXPLICIT INTENT -> launchEditActivity
    public void launchEditActivity(int position, View view) {

        //Toast
        Toast.makeText(getApplicationContext(), "Edit mode " , Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, EditItemActivity.class); /** try with getApplicationContext() **/

        //BUNDLE
        i.putExtra("listItem", todoItems.get(position).toString());
        i.putExtra("position", position);


        startActivityForResult(i, REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == REQUEST_CODE ) {
            if( resultCode == RESULT_OK ){
                String updatedItem = data.getStringExtra("updatedItem");
                Integer positionOfItem = data.getIntExtra("positionOfItem", 0);
                Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_LONG).show();

                //Remove original item
                todoAdapter.remove(todoItems.get(positionOfItem));

                //Add updated item in its place
                todoAdapter.insert(updatedItem, positionOfItem);

                //Refresh view
                todoAdapter.notifyDataSetChanged();
                //Save
                writeItems();


            }
        }
    }




    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://dot.todo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://dot.todo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
