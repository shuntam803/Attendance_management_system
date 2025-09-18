# Attendance_management_system

勤怠管理アプリを Spring Boot (REST API) + 静的フロントエンドで構築しています。

## 使い方

- 実行: `./gradlew bootRun`
- ビルド: `./gradlew bootWar` で実行可能 WAR を作成

アプリは組み込み Tomcat 上で REST API を公開し、`src/main/resources/static` 配下の HTML/JS/CSS を配信します。
ブラウザから `http://localhost:8080/index.html` にアクセスすると従業員・管理者メニューへ遷移できます。

## データベース設定

PostgreSQL 接続情報は環境変数またはシステムプロパティで上書きできます（未指定時はデフォルト値）。

- 環境変数
  - `APP_DB_URL` 例: `jdbc:postgresql://localhost:5432/attendance`
  - `APP_DB_USER` 例: `postgres`
  - `APP_DB_PASS` 例: `postprePass`

または JVM 引数（例）:

`-DAPP_DB_URL=jdbc:postgresql://localhost:5432/attendance -DAPP_DB_USER=postgres -DAPP_DB_PASS=postprePass`

## フロントエンド構成

- 静的ファイルは `src/main/resources/static` に配置されています。
  - 従業員向け画面: `static/attendance/*.html`
  - 管理者向け画面: `static/admin/**/*.html`
- JavaScript は用途別に以下へ集約しています。
  - `static/JS/attendance-app.js`
  - `static/JS/admin-app.js`
  - 共通ユーティリティ: `static/JS/func.js`

これらのファイルが Spring Boot の静的リソースとして配信され、REST API (`/attendance/**`, `/admin/**`) と通信します。
