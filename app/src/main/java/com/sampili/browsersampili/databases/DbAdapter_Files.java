/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package com.sampili.browsersampili.databases;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.sampili.browsersampili.R;


public class DbAdapter_Files {

    //define static variable
    private static final int dbVersion =6;
    private static final String dbName = "files_DB_v01.db";
    private static final String dbTable = "files";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+dbTable+" (_id INTEGER PRIMARY KEY autoincrement, files_title, files_content, files_icon, files_attachment, files_creation, UNIQUE(files_title))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+dbTable);
            onCreate(db);
        }
    }

    //establish connection with SQLiteDataBase
    private final Context c;
    private SQLiteDatabase sqlDb;

    public DbAdapter_Files(Context context) {
        this.c = context;
    }
    public void open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(c);
        sqlDb = dbHelper.getWritableDatabase();
    }

    //insert data
    @SuppressWarnings("SameParameterValue")
    public void insert(String files_title, String files_content, String files_icon, String files_attachment, String files_creation) {
        if(!isExist(files_title)) {
            sqlDb.execSQL("INSERT INTO files (files_title, files_content, files_icon, files_attachment, files_creation) VALUES('" + files_title + "','" + files_content + "','" + files_icon + "','" + files_attachment + "','" + files_creation + "')");
        }
    }
    //check entry already in database or not
    public boolean isExist(String files_title){
        String query = "SELECT files_title FROM files WHERE files_title='"+files_title+"' LIMIT 1";
        @SuppressLint("Recycle") Cursor row = sqlDb.rawQuery(query, null);
        return row.moveToFirst();
    }

    //fetch data
    public Cursor fetchAllData(Context context) {

        PreferenceManager.setDefaultValues(context, R.xml.user_settings, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String[] columns = new String[]{"_id", "files_title", "files_content", "files_icon","files_attachment","files_creation"};

        if (sp.getString("sortDBF", "title").equals("title")) {
            return sqlDb.query(dbTable, columns, null, null, null, null, "files_title" + " COLLATE NOCASE ASC;");
        } else if (sp.getString("sortDBF", "title").equals("file_Size")) {

            String orderBy = "files_content" + "," +
                    "files_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
        } else if (sp.getString("sortDBF", "title").equals("file_ext")) {

            String orderBy = "files_icon" + "," +
                    "files_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
        } else if (sp.getString("sortDBF", "title").equals("file_date")) {

            String orderBy = "files_creation" + "," +
                    "files_title" + " COLLATE NOCASE ASC;";

            return sqlDb.query(dbTable, columns, null, null, null, null, orderBy);
        }

        return null;
    }

    //fetch data by filter
    public Cursor fetchDataByFilter(String inputText,String filterColumn) throws SQLException {
        Cursor row;
        String query = "SELECT * FROM "+dbTable;
        if (inputText == null  ||  inputText.length () == 0)  {
            row = sqlDb.rawQuery(query, null);
        }else {
            query = "SELECT * FROM "+dbTable+" WHERE "+filterColumn+" like '%"+inputText+"%'";
            row = sqlDb.rawQuery(query, null);
        }
        if (row != null) {
            row.moveToFirst();
        }
        return row;
    }
}