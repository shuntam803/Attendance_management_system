const navigationSelector = '[data-link]';

const attendanceHandlers = {
  'attendance-login': initAttendanceLogin,
  'attendance-menu': initAttendanceMenu,
  'attendance-timecard': initAttendanceTimecard,
  'attendance-timesheet': initAttendanceTimesheet
};

document.addEventListener('DOMContentLoaded', () => {
  const page = document.body.dataset.page;
  const handler = attendanceHandlers[page];
  if (handler) {
    handler();
  }
  registerNavigationHandlers();
});

function registerNavigationHandlers() {
  document.querySelectorAll(navigationSelector).forEach((element) => {
    element.addEventListener('click', () => {
      const link = element.getAttribute('data-link');
      if (link) {
        window.location.href = link;
      }
    });
  });
}

function showMessageBox(box, textElement, message) {
  if (!box || !textElement) return;
  if (!message) {
    box.hidden = true;
    textElement.textContent = '';
    return;
  }
  textElement.textContent = message;
  box.hidden = false;
}

// === Attendance Login ===
function initAttendanceLogin() {
  const form = document.getElementById('login-form');
  const errorBox = document.getElementById('error-message');
  const errorText = errorBox?.querySelector('.error');

  const showError = (message) => showMessageBox(errorBox, errorText, message);
  const hideError = () => showMessageBox(errorBox, errorText, null);

  async function checkSession() {
    try {
      const response = await fetch('/attendance/login', { credentials: 'include' });
      if (!response.ok) return;
      const data = await response.json();
      if (data.loggedIn) {
        window.location.href = '/attendance/menu.html';
      }
    } catch (error) {
      console.warn('Failed to verify attendance session', error);
    }
  }

  async function login(event) {
    event.preventDefault();
    hideError();
    if (!form) return;

    const employeeCodeInput = document.getElementById('employeeCode');
    const passwordInput = document.getElementById('password');
    const employeeCode = employeeCodeInput instanceof HTMLInputElement ? employeeCodeInput.value.trim() : '';
    const password = passwordInput instanceof HTMLInputElement ? passwordInput.value : '';

    if (!employeeCode || !password) {
      showError('従業員コードとパスワードを入力してください。');
      return;
    }

    try {
      const response = await fetch('/attendance/login', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ employeeCode, password })
      });

      if (!response.ok) {
        const text = await response.text();
        try {
          const parsed = JSON.parse(text);
          showError(parsed.message || 'ログインに失敗しました。');
        } catch (error) {
          console.warn('Failed to parse login error response', error);
          showError(text || 'ログインに失敗しました。');
        }
        return;
      }

      const data = await response.json();
      if (data.success) {
        window.location.href = '/attendance/menu.html';
      } else {
        showError(data.message ?? 'ログインに失敗しました。');
      }
    } catch (error) {
      console.error(error);
      showError('通信に失敗しました。');
    }
  }

  form?.addEventListener('submit', login);
  form?.addEventListener('reset', hideError);
  checkSession();
}

// === Attendance Menu ===
function initAttendanceMenu() {
  const errorBox = document.getElementById('menu-error');
  const errorText = errorBox?.querySelector('.error');
  const logoutButton = document.getElementById('logout-button');

  const showError = (message) => showMessageBox(errorBox, errorText, message);

  async function ensureSession() {
    try {
      const response = await fetch('/attendance/menu', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/attendance/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error(`unexpected status: ${response.status}`);
      }
      showError(null);
    } catch (error) {
      console.error(error);
      showError('セッション確認に失敗しました。再度ログインしてください。');
      setTimeout(() => {
        window.location.href = '/attendance/login.html';
      }, 1500);
    }
  }

  async function logout() {
    try {
      const response = await fetch('/attendance/logout', {
        method: 'POST',
        credentials: 'include'
      });
      if (!response.ok) {
        throw new Error('logout failed');
      }
    } catch (error) {
      console.error(error);
      showError('ログアウトに失敗しました。');
      return;
    }
    window.location.href = '/attendance/login.html';
  }

  logoutButton?.addEventListener('click', logout);
  ensureSession();
}

// === Attendance Timecard ===
function initAttendanceTimecard() {
  const messageBox = document.getElementById('timecard-message');
  const messageText = messageBox?.querySelector('.complete');
  const errorBox = document.getElementById('timecard-error');
  const errorText = errorBox?.querySelector('.error');
  const buttons = {
    clockIn: document.getElementById('clock-in'),
    clockOut: document.getElementById('clock-out'),
    breakStart: document.getElementById('break-start'),
    breakEnd: document.getElementById('break-end')
  };

  const showSuccess = (message) => showMessageBox(messageBox, messageText, message);
  const showError = (message) => showMessageBox(errorBox, errorText, message);

  async function loadStatus() {
    try {
      const response = await fetch('/attendance/timecard', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/attendance/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error('status load failed');
      }
      const data = await response.json();
      updateDate();
      applyStatus(data.status);
      if (data.message) showSuccess(data.message); else showSuccess(null);
      if (data.error) showError(data.error); else showError(null);
    } catch (error) {
      console.error(error);
      showError('現在の勤怠状況を取得できませんでした。');
    }
  }

  function updateDate() {
    const dateLabel = document.getElementById('current-date');
    if (dateLabel) {
      const now = new Date();
      const formatted = new Intl.DateTimeFormat('ja-JP', { dateStyle: 'full' }).format(now);
      dateLabel.textContent = formatted;
    }
  }

  function applyStatus(status) {
    if (!status) return;
    setDisabled(buttons.clockIn, status.clockedIn);
    setDisabled(buttons.clockOut, !status.clockedIn || status.clockedOut || (status.breakStarted && !status.breakFinished));
    setDisabled(buttons.breakStart, status.clockedOut || !status.clockedIn || status.breakStarted);
    setDisabled(buttons.breakEnd, status.clockedOut || !status.breakStarted || status.breakFinished);
  }

  function setDisabled(element, value) {
    if (element instanceof HTMLButtonElement) {
      element.disabled = Boolean(value);
    }
  }

  async function sendAction(action) {
    showSuccess(null);
    showError(null);
    try {
      const response = await fetch('/attendance/timecard', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action })
      });
      const body = await response.json().catch(() => ({}));
      if (response.ok) {
        showSuccess(body.message || '処理が完了しました。');
        await loadStatus();
      } else {
        showError(body.message || body.error || '処理に失敗しました。');
      }
    } catch (error) {
      console.error(error);
      showError('通信に失敗しました。');
    }
  }

  if (buttons.clockIn) buttons.clockIn.addEventListener('click', () => sendAction('clockIn'));
  if (buttons.clockOut) buttons.clockOut.addEventListener('click', () => sendAction('clockOut'));
  if (buttons.breakStart) buttons.breakStart.addEventListener('click', () => sendAction('startBreak'));
  if (buttons.breakEnd) buttons.breakEnd.addEventListener('click', () => sendAction('finishBreak'));

  loadStatus();
}

// === Attendance Timesheet ===
function initAttendanceTimesheet() {
  const form = document.getElementById('timesheet-form');
  const selectMonth = document.getElementById('timesheet-month');
  const errorBox = document.getElementById('timesheet-error');
  const errorText = errorBox?.querySelector('.error');
  const viewSection = document.getElementById('timesheet-view');
  const tableBody = document.querySelector('#timesheet-table tbody');
  const employeeNameEl = document.getElementById('employee-name');
  const selectedMonthEl = document.getElementById('selected-month');

  const showError = (message) => showMessageBox(errorBox, errorText, message);

  function clearTable() {
    if (!tableBody) return;
    tableBody.innerHTML = '';
  }

  function fillOptions(months, selected) {
    if (!(selectMonth instanceof HTMLSelectElement)) return;
    selectMonth.innerHTML = '';

    const placeholder = document.createElement('option');
    placeholder.value = '';
    placeholder.disabled = true;
    placeholder.textContent = '選択してください';
    selectMonth.appendChild(placeholder);

    months.forEach((month) => {
      const option = document.createElement('option');
      option.value = month;
      option.textContent = month;
      selectMonth.appendChild(option);
    });

    const targetValue = selected && selected.length > 0
      ? selected
      : (months.length > 0 ? months[0] : '');

    if (targetValue && months.includes(targetValue)) {
      selectMonth.value = targetValue;
    } else {
      placeholder.selected = true;
    }
  }

  function formatTime(value) {
    if (!value) return '';
    return value.slice(0, 5).replace(':', '時') + '分';
  }

  function renderTimesheet(timesheet) {
    if (!timesheet) return;
    const { employeeName, selectedMonth, monthDays, workTimeMap } = timesheet;
    if (employeeNameEl) {
      employeeNameEl.textContent = employeeName || '';
    }
    if (selectedMonthEl) {
      if (selectedMonth) {
        const [year, month] = selectedMonth.split('-');
        selectedMonthEl.textContent = `${year}年${month}月分`;
      } else {
        selectedMonthEl.textContent = '';
      }
    }

    clearTable();
    if (!tableBody) return;

    (monthDays || []).forEach((day) => {
      const row = document.createElement('tr');
      row.classList.add('main_table');
      const work = workTimeMap ? workTimeMap[String(day)] : undefined;

      const cells = [
        `${day}日`,
        formatTime(work?.startTime),
        formatTime(work?.finishTime),
        formatTime(work?.breakStartTime),
        formatTime(work?.breakFinishTime),
        work?.breakTimeStr ?? '',
        work?.workingHoursStr ?? ''
      ];

      cells.forEach((value) => {
        const cell = document.createElement('td');
        cell.textContent = value ?? '';
        row.appendChild(cell);
      });
      tableBody.appendChild(row);
    });

    if (viewSection) {
      viewSection.hidden = false;
    }
  }

  async function loadOptions() {
    try {
      const response = await fetch('/attendance/timesheet', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/attendance/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error('failed to load months');
      }
      const data = await response.json();
      fillOptions(data.months ?? [], data.selectedMonth ?? '');
      showError(data.error);
    } catch (error) {
      console.error(error);
      showError('年月一覧の取得に失敗しました。');
    }
  }

  async function fetchTimesheet(event) {
    event.preventDefault();
    if (!(selectMonth instanceof HTMLSelectElement) || !selectMonth.value) {
      showError('年月を選択してください。');
      return;
    }
    showError('');
    try {
      const response = await fetch('/attendance/timesheet', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ month: selectMonth.value })
      });
      const body = await response.json().catch(() => ({}));
      if (response.status === 401) {
        window.location.href = '/attendance/login.html';
        return;
      }
      if (!response.ok) {
        showError(body.error || body.message || 'タイムシートの取得に失敗しました。');
        return;
      }
      renderTimesheet(body);
    } catch (error) {
      console.error(error);
      showError('タイムシートの取得に失敗しました。');
    }
  }

  form?.addEventListener('submit', fetchTimesheet);
  loadOptions();
}
