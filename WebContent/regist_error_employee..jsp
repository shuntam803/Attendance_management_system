<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="model.entity.Employee"%>

<%
	if (session.getAttribute("loginUserId") != null) {
		response.sendRedirect("login.jsp");
	} else {
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>管理者権限登録エラー</title>
<link rel="stylesheet" href="common/css/style.css">
</head>
<body>
	<div class="header">
		<span class="big_title">S</span>hun
		<span class="big_title">C</span>ub
	</div>
	<div class="menu">
		<div class="main_frame">
			<p>⚠すでに登録が存在しています。</p>
		</div>
	</div>
	<div class="main_wrapper">

		<div class="logout_button">
			<a href="regist_adminuser.jsp">
				<button class="display_button">もう一度管理者権限を付与をする</button>
			</a>
		</div>

		<div class="logout_button">
			<a href="menu.jsp">
				<button class="display_button">メニューへ戻る</button>
			</a>
		</div>
	</div>

	<div class="footer_top">
		<table class="table_format">
			<tr>
				<th>管理者情報</th>
			</tr>
			<tr>
				<td class="cel">会社名</td>
				<td>&nbsp;</td>
				<td>合同会社 しゅんカブ</td>
			</tr>
			<tr>
				<td class="cel">Tell</td>
				<td>&nbsp;</td>
				<td>080-0000-0000</td>
			<tr>
				<td class="cel">Email</td>
				<td>&nbsp;</td>
				<td>portforio@shuncub.com</td>
			</tr>
		</table>
	</div>

	<div class="footer_design">
		<footer>
			<small>© 2022 Shuncub.</small>
		</footer>
	</div>
</body>
</html>
<%
	}
%>