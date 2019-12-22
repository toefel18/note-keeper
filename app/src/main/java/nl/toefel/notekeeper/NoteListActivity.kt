package nl.toefel.notekeeper

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import nl.toefel.notekeeper.data.DataManager
import nl.toefel.notekeeper.data.NoteInfo

class NoteListActivity : AppCompatActivity() {

    lateinit var notesAdapter: ArrayAdapter<NoteInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        initializeDisplayContent()
    }

    private fun initializeDisplayContent() {
        val listNotes = findViewById<ListView>(R.id.list_notes)
        val notes = DataManager.getInstance().notes

        notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listNotes.adapter = notesAdapter

        listNotes.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(NoteActivity.NOTE_POSITION, position)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.notifyDataSetChanged()
    }
}
