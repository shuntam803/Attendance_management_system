# Attendance_management_system

勤怠管理アプリを Spring Boot + Thymeleaf で構築しています。

## 使い方

- 実行: `./gradlew bootRun` または `gradle bootRun`
- ビルド: `./gradlew bootWar` で実行可能 WAR を作成

アプリは組み込み Tomcat 上で Spring MVC コントローラと Thymeleaf テンプレートを利用して動作します。

## データベース設定

PostgreSQL 接続情報は環境変数またはシステムプロパティで上書きできます（未指定時はデフォルト値）。

- 環境変数
  - `APP_DB_URL` 例: `jdbc:postgresql://localhost:5432/attendance`
  - `APP_DB_USER` 例: `postgres`
  - `APP_DB_PASS` 例: `postprePass`

または JVM 引数（例）:

`-DAPP_DB_URL=jdbc:postgresql://localhost:5432/attendance -DAPP_DB_USER=postgres -DAPP_DB_PASS=postprePass`

## 備考

- 画面テンプレートは `src/main/resources/templates` 配下の Thymeleaf (`.html`) で提供します。
- ビルドは WAR 形式（`bootWar`）。Spring MVC コントローラがルーティングを担います。
