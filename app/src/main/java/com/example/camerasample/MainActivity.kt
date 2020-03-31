package com.example.camerasample

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

//recyclerViewを表示する。GridLayoutで実装する




class MainActivity : AppCompatActivity(), MyRecyclerViewAdapter.MyRecyclerViewInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            sendEditScreen()
        }

        recyclerView.layoutManager = GridLayoutManager(this@MainActivity, 2)


    }

    override fun onResume() {
        super.onResume()
        val realm = Realm.getDefaultInstance()
        val results: RealmResults<PictureModel> =realm.where(PictureModel::class.java).findAll()

        val adapter = MyRecyclerViewAdapter(results).apply {
            myListener = this@MainActivity
        }
        recyclerView.adapter = adapter

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply{
            findItem(R.id.takePictureMenu).isVisible = true
            findItem(R.id.deleteMenu).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> true
            R.id.deleteMenu -> {
                //Todo 削除する
                // 共通の関数を実装する
            }
            R.id.takePictureMenu -> {
                //Todo 写真をとる
                sendEditScreen()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }


    private fun sendEditScreen() {
        val intent = Intent(this@MainActivity, EditActivity::class.java)
        startActivity(intent)
    }

    //MyRecyclerViewAdapter.MyRecyclerViewInterface#onClick
    override fun onClick(strPictureUri:String) {

        val intent = Intent(this@MainActivity, DetailActivity::class.java).apply {
            putExtra("strPictureUri", strPictureUri)
        }
        startActivity(intent)
    }


}
