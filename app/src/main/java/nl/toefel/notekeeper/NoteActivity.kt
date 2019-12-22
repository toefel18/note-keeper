package nl.toefel.notekeeper

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import nl.toefel.notekeeper.data.CourseInfo
import nl.toefel.notekeeper.data.DataManager
import nl.toefel.notekeeper.data.NoteInfo
import java.io.File
import java.time.LocalDateTime

class NoteActivity : AppCompatActivity() {

    var noteInfo: NoteInfo? = null
    var isNewNote: Boolean = true
    var position: Int = POSITION_NOT_SET
    var isCancelling: Boolean = false
    var photoFile: String? = null

    lateinit var coursesSpinner: Spinner
    lateinit var adapterCourses: ArrayAdapter<CourseInfo>
    lateinit var titleText: EditText
    lateinit var contentText: EditText
    lateinit var noteImage: ImageView


    companion object {
        @JvmStatic
        val NOTE_POSITION = "nl.toefel.notekeeper.NOTE_POSITION";
        const val POSITION_NOT_SET = -1
        const val SHOW_CAMERA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(findViewById(R.id.toolbar))

        coursesSpinner = findViewById(R.id.spinner_courses)
        adapterCourses = ArrayAdapter(this, android.R.layout.simple_spinner_item, DataManager.getInstance().courses)
        noteImage = findViewById(R.id.note_image)

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coursesSpinner.adapter = adapterCourses
        titleText = findViewById(R.id.text_note_title)
        contentText = findViewById(R.id.text_note_content)

        readDisplayStateValues();

        if (!isNewNote) {
            displayNote()
        }
    }

    private fun displayNote() {
        val courses = DataManager.getInstance().courses
        coursesSpinner.apply { setSelection(courses.indexOf(noteInfo?.course)) }
        titleText.apply { setText(noteInfo?.title) }
        contentText.apply { setText(noteInfo?.text) }
        if (noteInfo?.imageUri != null) {
            noteImage.setImageURI(noteInfo!!.imageUri.toUri())
        }
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
        if (!isCancelling) {
            noteInfo?.apply {
                course = coursesSpinner.selectedItem as CourseInfo
                title = titleText.text.toString()
                text = contentText.text.toString()
            }
        } else if (isCancelling && isNewNote) {
            DataManager.getInstance().removeNote(position)
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
            R.id.action_cancel -> cancel()
            R.id.action_take_picture -> takePicture()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun takePicture(): Boolean {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("NOTE_KEEPER-${LocalDateTime.now()}", ".jpg", dir)
        photoFile = file.absolutePath
        val uri = FileProvider.getUriForFile(this, "nl.toefel.notekeeper.fileprovider", file)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, SHOW_CAMERA)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (requestCode == SHOW_CAMERA && resultCode == RESULT_OK) {
//            val thumbnail = result!!.getParcelableExtra<Bitmap>("data")
            noteInfo?.apply { imageUri = photoFile }
            noteImage.setImageURI(photoFile?.toUri())
//            noteImage.setImageBitmap(thumbnail)
        }
    }

    private fun cancel(): Boolean {
        isCancelling = true
        finish()
        return true
    }

    private fun sendNoteAsEmail(): Boolean {
        val course = (coursesSpinner.selectedItem as CourseInfo).title
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
