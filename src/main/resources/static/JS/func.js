// 共通ユーティリティ
const setError = (element, commentEl) => {
	element.classList.add("error");
	if (commentEl) commentEl.style.display = "block";
};

const clearError = (element, commentEl) => {
	element.classList.remove("error");
	if (commentEl) commentEl.style.display = "none";
};

const isValidAlphanumeric = value => /^[0-9a-zA-Z]+$/.test(value);
const containsInvalidChars = value => /[{}<>.,/&]/.test(value);

// パスワードチェック
const validatePassword = (passwordEl, confirmationEl, commentPassword, commentConfirmation) => {
	let valid = true;

	if (passwordEl.value !== confirmationEl.value) {
		setError(confirmationEl, commentConfirmation);
		valid = false;
	} else {
		clearError(confirmationEl, commentConfirmation);
	}

	if (passwordEl.value.length < 8 || passwordEl.value.length > 32 || !isValidAlphanumeric(passwordEl.value)) {
		setError(passwordEl, commentPassword);
		valid = false;
	} else {
		clearError(passwordEl, commentPassword);
	}

	return valid;
};

// 日付チェック
const validateBirthDate = (birthDayEl, commentDate) => {
	const today = new Date().toISOString().split("T")[0];
	if (birthDayEl.value > today) {
		setError(birthDayEl, commentDate);
		return false;
	}
	clearError(birthDayEl, commentDate);
	return true;
};

// 名前系チェック
const validateName = (el, min, max, commentAll, commentError) => {
	if (el.value.length < min || el.value.length > max || containsInvalidChars(el.value)) {
		setError(el, containsInvalidChars(el.value) ? commentError : commentAll);
		return false;
	}
	clearError(el);
	return true;
};

// ユーザIDチェック
const validateUserId = (userIdEl, commentUserId) => {
	if (userIdEl.value.length < 4 || userIdEl.value.length > 24 || !isValidAlphanumeric(userIdEl.value)) {
		setError(userIdEl, commentUserId);
		return false;
	}
	clearError(userIdEl, commentUserId);
	return true;
};

// フォームチェック
const chk = () => {
	const lastName = document.getElementById("last_name");
	const firstName = document.getElementById("first_name");
	const lastKanaName = document.getElementById("last_kana_name");
	const firstKanaName = document.getElementById("first_kana_name");
	const birthDay = document.getElementById("birth_day");
	const hireDay = document.getElementById("hire_day");

	const password = document.getElementById("password");
	const confirmation = document.getElementById("confirmation");

	const commentPassword = document.getElementById("comment_password");
	const commentConfirmation = document.getElementById("comment_confirmation");
	const commentDate = document.getElementById("comment_date");
	const commentAll = document.getElementById("comment_show_all");
	const commentError = document.getElementById("comment_error");

	let valid = true;

	valid &= validatePassword(password, confirmation, commentPassword, commentConfirmation);
	valid &= validateBirthDate(birthDay, commentDate);
	valid &= validateName(firstKanaName, 1, 24, commentAll, commentError);
	valid &= validateName(lastKanaName, 1, 24, commentAll, commentError);
	valid &= validateName(firstName, 1, 16, commentAll, commentError);
	valid &= validateName(lastName, 1, 16, commentAll, commentError);

	return Boolean(valid);
};

const chkUser = () => {
	const userId = document.getElementById("user_id");
	const password = document.getElementById("password");
	const confirmation = document.getElementById("confirmation");

	const commentUserId = document.getElementById("comment_user_id");
	const commentPassword = document.getElementById("comment_password");
	const commentConfirmation = document.getElementById("comment_confirmation");

	let valid = true;

	valid &= validateUserId(userId, commentUserId);
	valid &= validatePassword(password, confirmation, commentPassword, commentConfirmation);

	return Boolean(valid);
};

const chkShowAll = () => {
	const employeeCodes = document.getElementsByName("employeeCode");
	const chkBtn = document.getElementsByName("chkBtn")[0];
	let checked = false;

	employeeCodes.forEach(el => {
		if (el.checked) {
			checked = true;
			if (chkBtn.value === "delete_submit") {
				checked = confirm("本当に削除しますか？");
			}
		}
	});

	if (!checked) {
		document.getElementById("comment_show_all").style.display = "block";
	}

	return checked;
};

const setValue = btn => {
	document.getElementsByName("chkBtn")[0].value = btn;
};

// 時計表示
const setTime = num => String(num).padStart(2, "0");

const showClock = () => {
	const target = document.getElementById("RealtimeClockArea");
	if (!target) {
		return;
	}
	const now = new Date();
	const msg = `${setTime(now.getHours())}:${setTime(now.getMinutes())}:${setTime(now.getSeconds())}`;
	target.textContent = msg;
};

if (document.getElementById("RealtimeClockArea")) {
	showClock();
	setInterval(showClock, 1000);
}
