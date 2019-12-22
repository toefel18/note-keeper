package nl.toefel.notekeeper

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_note_list.*
import nl.toefel.notekeeper.data.DataManager

class NoteListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }

        initializeDisplayContent()
    }

    private fun initializeDisplayContent() {
        val listNotes = findViewById<ListView>(R.id.list_notes)
        val notes = DataManager.getInstance().notes

        val notesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listNotes.adapter = notesAdapter

        listNotes.setOnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(NoteActivity.NOTE_POSITION, position)
            startActivity(intent)
        }

    }

}
