package com.example.camerasample

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentProvider
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_edit.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

//パーミッション関係を攻略することが肝


class EditActivity : AppCompatActivity() {

    lateinit var uri:Uri
    var isCameraEnabled = false
    var isWriteStorageEnabled = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                launchCamera()

            }else{ permissionCheck() }

        }

        //写真の起動

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            launchCamera()
            return
        }
        permissionCheck()
    }

    private fun launchCamera() {
        //パーミッションの許可があるものだけがこちらを通る
        val dateStamp = SimpleDateFormat("yyyyMMdd_HHmmss_z").format(Date())
        uri = makeUri(dateStamp)

        println("URIの確認"+ uri)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        startActivityForResult(intent, PICTURE_CODE)
    }



    fun permissionCheck(){
        val permissionCheckCamera:Int = ContextCompat.checkSelfPermission(this@EditActivity, android.Manifest.permission.CAMERA)
        val permissionCheckWriteStorage:Int = ContextCompat.checkSelfPermission(this@EditActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionCheckCamera == PackageManager.PERMISSION_GRANTED ) isCameraEnabled = true
        if (permissionCheckWriteStorage == PackageManager.PERMISSION_GRANTED ) isWriteStorageEnabled = true

        if (isCameraEnabled && isWriteStorageEnabled) launchCamera() else requestPermission()

    }


    private fun requestPermission() {

        //パーミッションの説明をするかどうかを確認する
        val isNeedExplainOfCamera: Boolean = ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity, android.Manifest.permission.CAMERA)
        val isNeedExplainOfWriteStorage: Boolean
                = ActivityCompat.shouldShowRequestPermissionRationale(this@EditActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissions:ArrayList<String> = arrayListOf()
        if (!isCameraEnabled) permissions.add(android.Manifest.permission.CAMERA)
        if (!isWriteStorageEnabled) permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        if (!isNeedExplainOfCamera && !isNeedExplainOfWriteStorage){
            //パーミッションのリクエストを行う
            ActivityCompat.requestPermissions(this@EditActivity, permissions.toArray(arrayOfNulls(permissions.size)),REQUEST_CODE_PERMISSION)
            return
        }

        //ダイアログ内で許可を得たらリクエストを行う
        AlertDialog.Builder(this@EditActivity)
            .setTitle("写真の許可")
            .setMessage("写真撮影のために許可をするを押して下さい")
            .setPositiveButton("許可をする"){dialog, which ->
                ActivityCompat.requestPermissions(this@EditActivity, permissions.toArray(arrayOfNulls(permissions.size)),REQUEST_CODE_PERMISSION)
            }
            .setNegativeButton("許可をしない。"){dialog, which ->
                Toast.makeText(this, "許可が得られなかったので写真は取れません",Toast.LENGTH_SHORT).show()
                finish()
            }
            .show()
    }


    private fun makeUri(dateStamp:String): Uri {
        val appDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APPLICATION_NAME)

        //val appDir = File(this.externalMediaDirs,APPLICATION_NAME)

        appDir.mkdirs()
        val contentFilePath = appDir.path + "/" + dateStamp + ".jpg"
        //val contentFile = File(contentFilePath)
        val contentFile = File(appDir, dateStamp + ".jpg")
        println("PATHを表示"+contentFile)
        return FileProvider.getUriForFile(
            this@EditActivity,
            application.packageName + ".fileprovider",//applicationContext.packageName + ".fileprovider", //BuildConfig.APPLICATION_ID + ".fileprovider"  //applicationContext.packageName + ".fileprovider"
            contentFile)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSION) {
            finish()
            return
        }

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this@EditActivity, "これ以上は不可能", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        isCameraEnabled = true
        isWriteStorageEnabled = true
        launchCamera()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //写真が撮れたらこのアクティビティで写真を表示する
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PICTURE_CODE) return
        if (resultCode != Activity.RESULT_OK) return
        if ( uri == null ) return

        imageView.setImageURI(uri)

        //カメラアプリから戻ってきたらrealmに登録
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val newObj = realm.createObject(PictureModel::class.java)
        newObj.pictureUri = uri.toString()
        realm.commitTransaction()
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        menu.apply{
            findItem(R.id.takePictureMenu).isVisible = false
            findItem(R.id.deleteMenu).isVisible = false
        }
        return true
    }

}
