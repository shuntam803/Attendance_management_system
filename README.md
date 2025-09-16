# Attendance_management_system

勤怠管理アプリ

## 環境変数

アプリケーションを起動する前に、以下の環境変数または同名のシステムプロパティを設定してください。

| 変数名 | 内容 |
| ------ | ---- |
| `DB_URL` | 接続先データベースの JDBC URL |
| `DB_USER` | データベース接続で使用するユーザー名 |
| `DB_PASSWORD` | データベース接続で使用するパスワード |

例：

```bash
export DB_URL="jdbc:mysql://127.0.0.1/test_schema?useSSL=false"
export DB_USER="your_user"
export DB_PASSWORD="your_password"
```

アプリケーションサーバーを起動する際に環境変数が参照され、ソースコードに認証情報を保持する必要がなくなります。
