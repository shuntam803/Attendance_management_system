const adminNavigationSelector = '[data-link]';

const adminHandlers = {
  'admin-login': initAdminLogin,
  'admin-menu': initAdminMenu,
  'admin-register': initAdminRegister,
  'admin-employees-list': initAdminEmployeesList,
  'admin-employees-form': initAdminEmployeesForm,
  'admin-employees-delete': initAdminEmployeesDelete
};

document.addEventListener('DOMContentLoaded', () => {
  const page = document.body.dataset.page;
  const handler = adminHandlers[page];
  if (handler) {
    handler();
  }
  registerAdminNavigation();
});

function registerAdminNavigation() {
  document.querySelectorAll(adminNavigationSelector).forEach((element) => {
    element.addEventListener('click', () => {
      const link = element.getAttribute('data-link');
      if (link) {
        window.location.href = link;
      }
    });
  });
}

function toggleMessage(box, textElement, message) {
  if (!box || !textElement) return;
  if (!message) {
    box.hidden = true;
    textElement.textContent = '';
    return;
  }
  textElement.textContent = message;
  box.hidden = false;
}

// === Admin Login ===
function initAdminLogin() {
  const form = document.getElementById('admin-login-form');
  const errorBox = document.getElementById('admin-login-error');
  const errorText = errorBox?.querySelector('.error');

  const showError = (message) => toggleMessage(errorBox, errorText, message);
  const hideError = () => toggleMessage(errorBox, errorText, null);

  async function checkSession() {
    try {
      const response = await fetch('/admin/login', { credentials: 'include' });
      if (!response.ok) return;
      const data = await response.json();
      if (data.loggedIn) {
        window.location.href = '/admin/menu.html';
      }
    } catch (error) {
      console.warn('Failed to verify admin session', error);
    }
  }

  async function login(event) {
    event.preventDefault();
    hideError();
    if (!form) return;

    const userIdInput = document.getElementById('userId');
    const passwordInput = document.getElementById('password');
    const userId = userIdInput instanceof HTMLInputElement ? userIdInput.value.trim() : '';
    const password = passwordInput instanceof HTMLInputElement ? passwordInput.value : '';

    if (!userId || !password) {
      showError('ユーザIDとパスワードを入力してください。');
      return;
    }

    try {
      const response = await fetch('/admin/login', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, password })
      });

      const body = await response.json().catch(() => ({}));
      if (response.ok && body.success) {
        window.location.href = '/admin/menu.html';
        return;
      }

      showError(body.message || 'ユーザIDまたはパスワードが正しくありません。');
    } catch (error) {
      console.error(error);
      showError('通信に失敗しました。');
    }
  }

  form?.addEventListener('submit', login);
  form?.addEventListener('reset', hideError);
  checkSession();
}

// === Admin Menu ===
function initAdminMenu() {
  const errorBox = document.getElementById('admin-menu-error');
  const errorText = errorBox?.querySelector('.error');
  const logoutButton = document.getElementById('admin-logout');

  const showError = (message) => toggleMessage(errorBox, errorText, message);

  async function ensureSession() {
    try {
      const response = await fetch('/admin/menu', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error('status check failed');
      }
      showError(null);
    } catch (error) {
      console.error(error);
      showError('セッション確認に失敗しました。再度ログインしてください。');
      setTimeout(() => {
        window.location.href = '/admin/login.html';
      }, 1500);
    }
  }

  async function logout() {
    try {
      const response = await fetch('/admin/logout', {
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
    window.location.href = '/admin/login.html';
  }

  logoutButton?.addEventListener('click', logout);
  ensureSession();
}

// === Admin Register ===
function initAdminRegister() {
  const form = document.getElementById('admin-register-form');
  const errorBox = document.getElementById('admin-register-error');
  const errorText = errorBox?.querySelector('.error');
  const successBox = document.getElementById('admin-register-success');
  const successText = successBox?.querySelector('.complete');

  const showError = (message) => toggleMessage(errorBox, errorText, message);
  const hideError = () => toggleMessage(errorBox, errorText, null);
  const showSuccess = (message) => toggleMessage(successBox, successText, message);
  const hideSuccess = () => toggleMessage(successBox, successText, null);

  async function ensureSession() {
    try {
      const response = await fetch('/admin/users/new', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error('failed to load form');
      }
      const data = await response.json();
      showError(data.error || null);
    } catch (error) {
      console.error(error);
      showError('セッション確認に失敗しました。');
      setTimeout(() => {
        window.location.href = '/admin/login.html';
      }, 1500);
    }
  }

  async function register(event) {
    event.preventDefault();
    hideSuccess();
    hideError();
    if (typeof chkUser === 'function' && !chkUser()) {
      showError('入力内容を確認してください。');
      return;
    }
    const userIdInput = document.getElementById('user_id');
    const passwordInput = document.getElementById('password');
    const confirmationInput = document.getElementById('confirmation');
    const payload = {
      userId: userIdInput instanceof HTMLInputElement ? userIdInput.value : '',
      password: passwordInput instanceof HTMLInputElement ? passwordInput.value : '',
      confirmation: confirmationInput instanceof HTMLInputElement ? confirmationInput.value : ''
    };

    try {
      const response = await fetch('/admin/users', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const body = await response.json().catch(() => ({}));
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        showError(body.message || body.error || '登録に失敗しました。');
        return;
      }
      showSuccess(body.message || '管理者ユーザーを登録しました。');
      form?.reset();
      setTimeout(() => {
        window.location.href = '/admin/menu.html';
      }, 1200);
    } catch (error) {
      console.error(error);
      showError('登録処理に失敗しました。');
    }
  }

  form?.addEventListener('submit', register);
  form?.addEventListener('reset', () => {
    hideError();
    hideSuccess();
  });

  ensureSession();
}

// === Admin Employees List ===
function initAdminEmployeesList() {
  const errorBox = document.getElementById('employee-list-error');
  const errorText = errorBox?.querySelector('.error');
  const tableBody = document.querySelector('#employee-table tbody');
  const actionButtons = document.querySelectorAll('[data-action]');

  const showError = (message) => toggleMessage(errorBox, errorText, message);

  function formatDate(value) {
    if (!value) return '';
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return '';
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  function renderEmployees(employees) {
    if (!tableBody) return;
    tableBody.innerHTML = '';
    employees.forEach((employee) => {
      const row = document.createElement('tr');
      row.classList.add('main_table');

      const radioCell = document.createElement('td');
      const radio = document.createElement('input');
      radio.type = 'radio';
      radio.name = 'employeeCode';
      radio.value = employee.employeeCode;
      radioCell.appendChild(radio);

      row.appendChild(radioCell);
      row.appendChild(createCell(employee.employeeCode));
      row.appendChild(createCell(employee.employeeName));
      row.appendChild(createCell(employee.employeeKanaName));
      row.appendChild(createCell(employee.gender));
      row.appendChild(createCell(formatDate(employee.birthDay)));
      row.appendChild(createCell(employee.sectionName));
      row.appendChild(createCell(formatDate(employee.hireDate)));

      tableBody.appendChild(row);
    });
  }

  function createCell(text) {
    const cell = document.createElement('td');
    cell.textContent = text ?? '';
    return cell;
  }

  function getSelectedCode() {
    const selected = document.querySelector('input[name="employeeCode"]:checked');
    return selected instanceof HTMLInputElement ? selected.value : null;
  }

  async function loadEmployees() {
    try {
      const response = await fetch('/admin/employees', { credentials: 'include' });
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        throw new Error('failed to load employees');
      }
      const data = await response.json();
      showError(data.error || null);
      renderEmployees(data.employees || []);
    } catch (error) {
      console.error(error);
      showError('従業員一覧の取得に失敗しました。');
    }
  }

  actionButtons.forEach((button) => {
    button.addEventListener('click', () => {
      const action = button.getAttribute('data-action');
      const code = getSelectedCode();
      if (!code) {
        showError('従業員を選択してください。');
        return;
      }
      if (action === 'edit') {
        window.location.href = `/admin/employees/edit.html?code=${encodeURIComponent(code)}`;
      } else if (action === 'delete') {
        window.location.href = `/admin/employees/delete.html?code=${encodeURIComponent(code)}`;
      }
    });
  });

  loadEmployees();
}

// === Admin Employees Form (new/edit) ===
function initAdminEmployeesForm() {
  const form = document.getElementById('employee-form');
  const errorBox = document.getElementById('employee-form-error');
  const errorText = errorBox?.querySelector('.error');
  const successBox = document.getElementById('employee-form-success');
  const successText = successBox?.querySelector('.complete');
  const sectionSelect = document.getElementById('section_code');
  const employeeCodeDisplay = document.getElementById('employee_code_display');
  let employeeCode = null;

  const mode = document.body.dataset.formMode || 'create';

  const showError = (message) => toggleMessage(errorBox, errorText, message);
  const hideError = () => toggleMessage(errorBox, errorText, null);
  const showSuccess = (message) => toggleMessage(successBox, successText, message);
  const hideSuccess = () => toggleMessage(successBox, successText, null);

  function setValue(id, value) {
    const element = document.getElementById(id);
    if (element instanceof HTMLInputElement) {
      element.value = value ?? '';
    }
  }

  function setRadioValue(name, value) {
    document.querySelectorAll(`input[name="${name}"]`).forEach((radio) => {
      if (radio instanceof HTMLInputElement) {
        radio.checked = radio.value === String(value ?? '');
      }
    });
  }

  function getValue(id) {
    const element = document.getElementById(id);
    return element instanceof HTMLInputElement ? element.value : '';
  }

  function getRadioValue(name) {
    const selected = document.querySelector(`input[name="${name}"]:checked`);
    return selected instanceof HTMLInputElement ? selected.value : '';
  }

  function fillSections(sections) {
    if (!(sectionSelect instanceof HTMLSelectElement)) return;
    sectionSelect.innerHTML = '';
    sections.forEach((section) => {
      const option = document.createElement('option');
      option.value = section.sectionCode;
      option.textContent = section.sectionName;
      sectionSelect.appendChild(option);
    });
  }

  function populateForm(formData) {
    setValue('last_name', formData.lastName);
    setValue('first_name', formData.firstName);
    setValue('last_kana_name', formData.lastKanaName);
    setValue('first_kana_name', formData.firstKanaName);
    setRadioValue('gender', formData.gender);
    if (sectionSelect instanceof HTMLSelectElement && formData.sectionCode) {
      sectionSelect.value = formData.sectionCode;
    }
    setValue('birth_day', formData.birthDay);
    setValue('hire_day', formData.hireDate);
    if (employeeCodeDisplay) {
      employeeCodeDisplay.textContent = formData.employeeCode || employeeCode;
    }
    employeeCode = formData.employeeCode || employeeCode;
  }

  function validateEditForm() {
    return [
      getValue('last_name'),
      getValue('first_name'),
      getValue('last_kana_name'),
      getValue('first_kana_name'),
      getRadioValue('gender'),
      getValue('birth_day'),
      sectionSelect instanceof HTMLSelectElement ? sectionSelect.value : '',
      getValue('hire_day')
    ].every((value) => value && value.trim().length > 0);
  }

  async function loadForm() {
    try {
      if (mode === 'edit') {
        const params = new URLSearchParams(window.location.search);
        employeeCode = params.get('code');
        if (!employeeCode) {
          showError('従業員コードが指定されていません。');
          return;
        }
        const response = await fetch(`/admin/employees/${encodeURIComponent(employeeCode)}/edit`, { credentials: 'include' });
        if (response.status === 401) {
          window.location.href = '/admin/login.html';
          return;
        }
        if (response.status === 404) {
          showError('従業員が見つかりません。');
          return;
        }
        if (!response.ok) {
          throw new Error('failed to load edit form');
        }
        const data = await response.json();
        showError(data.error || null);
        fillSections(data.sections || []);
        populateForm(data.form || {});
      } else {
        const response = await fetch('/admin/employees/new', { credentials: 'include' });
        if (response.status === 401) {
          window.location.href = '/admin/login.html';
          return;
        }
        if (!response.ok) {
          throw new Error('failed to load create form');
        }
        const data = await response.json();
        showError(data.error || null);
        fillSections(data.sections || []);
      }
    } catch (error) {
      console.error(error);
      showError('フォーム情報の取得に失敗しました。');
      if (mode === 'edit') {
        setTimeout(() => {
          window.location.href = '/admin/employees/index.html';
        }, 1500);
      }
    }
  }

  function buildPayload() {
    const payload = {
      lastName: getValue('last_name'),
      firstName: getValue('first_name'),
      lastKanaName: getValue('last_kana_name'),
      firstKanaName: getValue('first_kana_name'),
      gender: getRadioValue('gender'),
      sectionCode: sectionSelect instanceof HTMLSelectElement ? sectionSelect.value : '',
      birthDay: getValue('birth_day'),
      hireDate: getValue('hire_day'),
      password: getValue('password'),
      confirmation: getValue('confirmation')
    };
    if (mode === 'edit') {
      payload.employeeCode = employeeCode;
      delete payload.password;
      delete payload.confirmation;
    }
    return payload;
  }

  async function submitForm(event) {
    event.preventDefault();
    hideError();
    hideSuccess();

    if (mode === 'create' && typeof chk === 'function' && !chk()) {
      showError('入力内容を確認してください。');
      return;
    }
    if (mode === 'edit' && !validateEditForm()) {
      showError('必須項目を入力してください。');
      return;
    }

    const payload = buildPayload();
    let url = '/admin/employees';
    let method = 'POST';
    if (mode === 'edit') {
      if (!employeeCode) {
        showError('従業員コードが取得できません。');
        return;
      }
      url = `/admin/employees/${encodeURIComponent(employeeCode)}/edit`;
    }

    try {
      const response = await fetch(url, {
        method,
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const body = await response.json().catch(() => ({}));
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        showError(body.message || body.error || '処理に失敗しました。');
        return;
      }
      showSuccess(body.message || (mode === 'edit' ? '従業員情報を更新しました。' : '従業員を登録しました。'));
      if (mode === 'create') {
        form?.reset();
      }
      setTimeout(() => {
        window.location.href = '/admin/employees/index.html';
      }, 1200);
    } catch (error) {
      console.error(error);
      showError('通信に失敗しました。');
    }
  }

  form?.addEventListener('submit', submitForm);
  form?.addEventListener('reset', () => {
    hideError();
    hideSuccess();
  });

  loadForm();
}

// === Admin Employees Delete ===
function initAdminEmployeesDelete() {
  const errorBox = document.getElementById('employee-delete-error');
  const errorText = errorBox?.querySelector('.error');
  const messageBox = document.getElementById('employee-delete-message');
  const messageText = messageBox?.querySelector('.complete');
  const deleteButton = document.getElementById('delete-confirm');
  const codeEl = document.getElementById('delete-code');
  const nameEl = document.getElementById('delete-name');
  const sectionEl = document.getElementById('delete-section');
  const hireEl = document.getElementById('delete-hire');
  let employeeCode = null;

  const showError = (message) => toggleMessage(errorBox, errorText, message);
  const showMessage = (message) => toggleMessage(messageBox, messageText, message);

  async function loadEmployee() {
    const params = new URLSearchParams(window.location.search);
    employeeCode = params.get('code');
    if (!employeeCode) {
      showError('従業員コードが指定されていません。');
      return;
    }
    try {
      const response = await fetch(`/admin/employees/${encodeURIComponent(employeeCode)}/delete`, {
        credentials: 'include'
      });
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (response.status === 404) {
        showError('従業員が見つかりません。');
        return;
      }
      if (!response.ok) {
        throw new Error('failed to load delete info');
      }
      const data = await response.json();
      if (data.error) {
        showError(data.error);
        return;
      }
      const employee = data.employee || {};
      if (codeEl) codeEl.textContent = employee.employeeCode || employeeCode;
      if (nameEl) nameEl.textContent = `${employee.lastName ?? ''} ${employee.firstName ?? ''}`.trim();
      if (sectionEl) sectionEl.textContent = employee.sectionCode ?? '';
      if (hireEl) hireEl.textContent = employee.hireDate ?? '';
      showError(null);
    } catch (error) {
      console.error(error);
      showError('従業員情報の取得に失敗しました。');
    }
  }

  async function deleteEmployee() {
    if (!employeeCode) {
      showError('従業員コードが指定されていません。');
      return;
    }
    try {
      const response = await fetch(`/admin/employees/${encodeURIComponent(employeeCode)}/delete`, {
        method: 'POST',
        credentials: 'include'
      });
      const body = await response.json().catch(() => ({}));
      if (response.status === 401) {
        window.location.href = '/admin/login.html';
        return;
      }
      if (!response.ok) {
        showError(body.message || body.error || '削除に失敗しました。');
        return;
      }
      showMessage(body.message || '従業員を削除しました。');
      setTimeout(() => {
        window.location.href = '/admin/employees/index.html';
      }, 1200);
    } catch (error) {
      console.error(error);
      showError('削除に失敗しました。');
    }
  }

  deleteButton?.addEventListener('click', deleteEmployee);
  loadEmployee();
}
