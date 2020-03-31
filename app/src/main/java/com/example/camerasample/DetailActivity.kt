package com.example.camerasample

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    var strPictureUri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)


        strPictureUri = intent.extras!!.getString("strPictureUri")!!
        imageViewDetail.setImageURI(Uri.parse(strPictureUri))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply{
            findItem(R.id.takePictureMenu).isVisible = false
            findItem(R.id.deleteMenu).isVisible = true
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.deleteMenu ->{ deletePicture(strPictureUri) }
            else ->{super.onOptionsItemSelected(item)}
        }
        return true
    }


    private fun deletePicture(strUri:String) {

        //共有ストレージから当該写真を削除
        contentResolver.delete(Uri.parse(strUri), null, null)

        //realmからuri:Stringを削除
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val obj = realm.where(PictureModel::class.java).equalTo(PictureModel::pictureUri.name, strUri).findFirst()
        obj?.deleteFromRealm()
        realm.commitTransaction()
        realm.close()

        //削除完了メッセージを表示
        Toast.makeText(this@DetailActivity, "削除しました", Toast.LENGTH_SHORT).show()

        finish()
    }


}
