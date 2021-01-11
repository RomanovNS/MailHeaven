document.addEventListener ('DOMContentLoaded', event =>
{
	const _registrationForm = document.querySelector ('#registrationForm');
	const _loginForm = document.querySelector ('#loginForm');
	const _registrationRef = document.querySelector ('#registrationRef');
	const _cancelRegistrationRef = document.querySelector ('#cancelRegistrationRef');

	_registrationRef.addEventListener ('click', event => _loginForm.style.display = 'none');
	_cancelRegistrationRef.addEventListener ('click', event => _loginForm.style.display = null);
});