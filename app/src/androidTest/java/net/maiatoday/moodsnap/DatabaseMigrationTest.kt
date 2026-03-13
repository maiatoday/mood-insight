package net.maiatoday.moodsnap

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import net.maiatoday.moodsnap.data.AppDatabase
import net.maiatoday.moodsnap.di.AppModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // Create version 2 of the database
        var db = helper.createDatabase(TEST_DB, 2)

        // Insert some data using SQL (since we can't use current DAOs for old version)
        // In version 2, we had a 'tags' column that was removed in version 3
        db.execSQL("""
            INSERT INTO mood_entries (id, moodScore, notes, movement, sunlight, sleep, energy, timestamp, tags)
            VALUES (1, 5, 'Great day', 1, 1, 1, 10, 1000, 'happy,sunny')
        """)
        db.close()

        // Re-open with Migration and version 3
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, AppModule.MIGRATION_2_3)

        // Verify data exists
        val cursor = db.query("SELECT * FROM mood_entries")
        assert(cursor.moveToFirst())
        assert(cursor.getInt(cursor.getColumnIndex("id")) == 1)
        assert(cursor.getInt(cursor.getColumnIndex("moodScore")) == 5)
        
        // Verify 'tags' column is gone
        assert(cursor.getColumnIndex("tags") == -1)
        
        cursor.close()
    }
}
