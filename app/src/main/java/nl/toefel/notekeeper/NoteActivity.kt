package nl.toefel.notekeeper

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.content_note.*
import nl.toefel.notekeeper.data.CourseInfo
import nl.toefel.notekeeper.data.DataManager
import nl.toefel.notekeeper.data.NoteInfo

class NoteActivity : AppCompatActivity() {

    var noteInfo: NoteInfo? = null
    var isNewNote: Boolean = true
    var position: Int = POSITION_NOT_SET
    lateinit var coursesSpinner: Spinner
    lateinit var adapterCourses: ArrayAdapter<CourseInfo>
    lateinit var titleText: EditText
    lateinit var contentText: EditText

    companion object {
        @JvmStatic
        val NOTE_POSITION = "nl.toefel.notekeeper.NOTE_POSITION";
        const val POSITION_NOT_SET = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(toolbar)

        coursesSpinner = findViewById<Spinner>(R.id.spinner_courses)
        adapterCourses = ArrayAdapter(this, android.R.layout.simple_spinner_item, DataManager.getInstance().courses)

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coursesSpinner.adapter = adapterCourses
        titleText = findViewById(R.id.text_note_title)
        contentText = findViewById(R.id.text_note_content)

        readDisplayStateValues();

        if (!isNewNote) {
            displayNote(coursesSpinner, titleText, contentText)
        }
    }

    private fun displayNote(coursesSpinner: Spinner?, titleText: EditText?, contentText: EditText?) {
        val courses = DataManager.getInstance().courses
        coursesSpinner?.apply { setSelection(courses.indexOf(noteInfo?.course)) }
        titleText?.apply { setText(noteInfo?.title) }
        contentText?.apply { setText(noteInfo?.text) }
    }

    private fun readDisplayStateValues() {
        val intent = getIntent()
        position = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = position == POSITION_NOT_SET

        if (isNewNote) {
            position = DataManager.getInstance().createNewNote()
        }
        noteInfo = DataManager.getInstance().notes[position]
    }

    override fun onPause() {
        super.onPause()
        noteInfo?.apply {
            course = coursesSpinner.selectedItem as CourseInfo
            title = titleText.text.toString()
            text = contentText.text.toString()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_send_email -> sendNoteAsEmail()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sendNoteAsEmail(): Boolean {
        val course = (spinner_courses.selectedItem as CourseInfo).title
        val title = titleText.text.toString()
        val content = contentText.text.toString()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc2822"
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, "Course: $course:\n\n Content: $content")
        startActivity(intent)

        return true;
    }
}
