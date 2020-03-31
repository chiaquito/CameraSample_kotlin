# CameraSample_kotlin
カメラを使う場合のパーミッション、FileProvider、写真の削除等のサンプル

## 得られた知見
1. カメラアプリを起動する際にパーミッションをマニフェストファイルに追記する事が必要。
  - uses-permission android:name="android.permission.CAMERA"
  - uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
2. ユーザーにパーミッションの許可を得る。
3. カメラアプリ起動のインテントにFileProviderによるuriオブジェクトを生成する(contentproviderスキームを付帯させる)
