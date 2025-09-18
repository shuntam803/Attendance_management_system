# Metabase バックアップと復元手順

- データベースはPostgreSQLを使用している場合を想定しています。
- 作成したダッシュボードの反映

## バックアップ


1**postgresユーザーにログイン**
   ```bash
   sudo -iu postgres
   ```

3. **データベースのダンプ**
   PostgreSQLのmetabasedbデータベースをダンプします。
   ```bash
   pg_dump -U postgres -h localhost -d attendance -v -f /var/lib/pgsql/attendance_backup_$(date '+%Y-%m-%d').sql
   ```
   ダンプする時にパスワードを求められます。


2**postgresユーザーのログアウト**
   ```bash
   logout
   ```

3**ダンプファイルのコピー**
   作成したダンプファイルを任意の場所にコピーします。
   ```bash
   sudo cp /var/lib/pgsql/attendance_backup_$(date '+%Y-%m-%d').sql /home/$(whoami)
   ```

## 復元
ダンプファイル(`attendance_backup_$(date '+%Y-%m-%d').sql`)をあらかじめ`/var/lib/pgsql/`に配置してください。

   ```
   sudo cp /home/$(whoami)/attendance_backup_$(date '+%Y-%m-%d').sql /var/lib/pgsql
   ```

1**postgresユーザーにログイン**
   ```bash
   sudo -iu postgres
   ```

2**attendanceデータベースの作成(適宜削除)**
   同じデータベース名を使用してattendanceデータベースを作成します。

   ```bash
   dropdb -U postgres attendance # すでにデータベースある場合は先に削除を行います。
   createdb -U postgres attendance
   ```

3**データベース(attendance)のリストア**
   PostgreSQLデータベースに先程のバックアップをリストアします。(sqlファイルは実際にバックアップした日付になります。)
   ```bash
   psql -U postgres -d attendance -f /var/lib/pgsql/attendance_backup_$(date '+%Y-%m-%d').sql
   ```

4**postgresユーザーのログアウト**
   ```bash
   logout
   ```

