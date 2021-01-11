/*
const random = (minValue, maxValue) => minValue + Math.round (Math.random () * (maxValue - minValue));

const __accountsJSON = 
[
	{
		eMail: 'nikita-romanov71@rambler.ru',
		folders:
		[
			{
				name: 'Входящие',
				count: 52,
			},
			{
				name: 'Отправленные',
				count: 5,
			},
			{
				name: 'Спам',
				count: 4,
			},
		],
	},
	{
		eMail: 'nikitaromanov71@gmail.com',
		folders:
		[
			{
				name: 'Входящие',
				count: 3456,
			},
			{
				name: 'Отправленные',
				count: 0,
			},
		],
	},
];

const __browserSectionJSON = (() =>
{
	const _words = ('Мода женщин джинсовые шорты Crimping High Waist Slim Летние джинсы Шорты Девушки Сексуальные Шорты Брюки Унисекс ' + 
	'человек женщина уникальный дизайн ретро древесины творческие простой Романтические пары кольца ювелирные изделия палец Band ' + 
	'Иногда продавцы ошибаются: высылают товар неверного цвета или другого размера. Если такое случилось с вашим заказом, обратитесь в ' + 
	'службу поддержки Joom! Мы рассмотрим вашу заявку и примем решение о частичном или полном возврате средств.	Как отправить заявку в службу ' + 
	'поддержки? Eсли вы читаете эту статью в приложении, нажмите на иконку чата в правом верхнем углу. Укажите номер заказа, опишите ' + 
	'полученный вами товар и приложите доказательство — фотографии вещи и почтовой упаковки. Если вы пользуетесь веб-сайтом Joom, пришлите ' + 
	'нам письмо по адресу support@joom.com. Не забудьте указать номер заказа, о котором идет речь, описать проблему и приложить фотографию ' + 
	'полученного товара. ВАЖНО: претензии по качеству принимаются в течение 30 дней после получения вами товара. ВАЖНО: прежде чем принять ' + 
	'решение по вашей заявке, агент службы поддержки может попросить вас предоставить дополнительные доказательства — например, фотографии ' + 
	'или видео').split (' ');

	const _result = 
	{
		lettersTotalCount: 500,
		letters: [],
	};

	for (let i = 0; i < _result.lettersTotalCount; i ++)
	{
		const _generate = wordsCount =>
		{
			let _result = '';
			for (let j = 0; j < wordsCount; j ++)
				_result += _words [random (0, _words.length - 1)] + ' ';

			return _result.trim ();
		};

		_result.letters.push (
		{
			date: '12.12.2012',
			time: '22:16',
			senderName: String (i) + ' ' + _generate (random (1, 3)),
			senderEmail: 'noreply@quora.com',
			recipientName: 'Nikita Romanov 71',
			recipientEmail: 'nikita.romanov@rambler.ru',
			title: _generate (random (2, 8)),
			html: _generate (random (20, 500)),
		});
	};

	return _result;
}) ();
*/

window.addEventListener ('load', event =>
{
	const _writeButton = document.querySelectorAll ('#nav > .navToken') [0];
	const _replyButton = document.querySelectorAll ('#nav > .navToken') [1];
	const _accountsContainer = document.querySelector ('#accountsContainer');
	const _addAccountButton = document.querySelector ('#addAccountButton');
	const _addAccountPopup = document.querySelector ('#addAccountPopup');
	const _addAccountPopupCloseButton = document.querySelector ('#addAccountPopupCloseButton');
	const _foldersContainer = document.querySelector ('#foldersContainer');
	const _foldersDummy = document.querySelector ('#foldersDummy');
	const _browserSectionTitle = document.querySelector ('#browserSectionHeader > span');
	const _browserPageControl = document.querySelector ('#browserPageControl');
	const _browserPageInput = document.querySelector ('#browserPageControl > input');
	const _browserSectionPageCount = document.querySelector ('#browserPageControl > span');
	const _browserRecordsContainer = document.querySelector ('#browserRecordsContainer');
	const _browserSectionDummy = document.querySelector ('#browserSection > .sectionDummy');
	const _letterReadBlockTitle = document.querySelector ('#letterTitle');
	const _letterReadBlockSenderName = document.querySelectorAll ('.letterTransportString') [0].querySelector ('.letterTransportName');
	const _letterReadBlockSenderEmail = document.querySelectorAll ('.letterTransportString') [0].querySelector ('.letterTransportEmail');
	const _letterReadBlockRecipientName = document.querySelectorAll ('.letterTransportString') [1].querySelector ('.letterTransportName');
	const _letterReadBlockRecipientEmail = document.querySelectorAll ('.letterTransportString') [1].querySelector ('.letterTransportEmail');
	const _letterReadBlockIframe = document.querySelector ('#letterReadBlock > iframe');
	const _letterWriteBlock = document.querySelector ('#letterWriteBlock');
	const _letterWriteFromEmail = document.querySelector ('#letterWriteFromEmail');
	const _letterWriteTo = document.querySelector ('#letterWriteTo');
	const _letterWriteTitle = document.querySelector ('#letterWriteTitle');
	const _letterWriteQuote = document.querySelector ('#quote');
	const _letterWriteTextarea = document.querySelector ('#letterWriteBlock > textarea');
	const _letterSectionDummy = document.querySelector ('#letterSection > .sectionDummy');

	const _accountRecordTemplate = document.querySelector ('template').content.querySelector ('.accountRecord');
	const _folderRecordTemplate = document.querySelector ('template').content.querySelector ('.folderRecord');
	const _browserRecordTemplate = document.querySelector ('template').content.querySelector ('.browserRecord');

	const _lettersPerPage = 24;

	const _current = 
	{
		accountEmail: null,
		folderName: null,
		folderPagesCount: null,
		letter: null,
	};

	const _clearElement = element =>
	{
		while (element.firstChild)
			element.firstChild.remove ();
	};

	const _loadAccounts = callback =>
	{
		const _request = new XMLHttpRequest ();

		_request.open ('GET', 'http://localhost:8080/getAccounts');
		_request.responseType = 'json';	
		_request.send ();
		_request.onload = () =>
		{
			if (_request.status === 200)
				callback (_request.response);
			else
				console.error ('ajaxRequestError');
		};

//		callback (__accountsJSON);
	};

	const _loadFolder = (eMail, folderName, fromIndex, toIndex, callback) =>
	{
		const _request = new XMLHttpRequest ();

		const _url = new URL ('/getFolder', 'http://localhost:8080');
		_url.searchParams.append ('accountEmail', eMail);
		_url.searchParams.append ('folderName', folderName);
		_url.searchParams.append ('fromIndex', fromIndex + 1);
		_url.searchParams.append ('toIndex', toIndex + 1);

		_request.open ('GET', _url);
		_request.responseType = 'json';
		_request.send ();
		_request.onload = () =>
		{
			if (_request.status === 200)
				callback (_request.response);
			else
				console.error ('ajaxRequestError');
		};

		// const _folderData = 
		// {
		// 	lettersTotalCount: __browserSectionJSON.lettersTotalCount,
		// 	letters: __browserSectionJSON.letters.slice (fromIndex, toIndex + 1),
		// };

		// callback (_folderData);
	};

	const _displayAccounts = accountsArray =>
	{
		const _accountRecords = [];

		for (const _account of accountsArray)
		{
			const _accountRecord = _accountRecordTemplate.cloneNode (true);
			_accountsContainer.append (_accountRecord);
			
			const _serviceName = _accountRecord.querySelector ('.controlSectionTokenName');
			const _eMail = _accountRecord.querySelector ('.accountEmail');

			let _serviceString = _account.eMail.split ('@') [1];
			_serviceString = _serviceString.slice (0, _serviceString.lastIndexOf ('.'));
			_serviceString = _serviceString.charAt (0).toUpperCase () + _serviceString.slice (1);

			_serviceName.textContent = _serviceString;
			_eMail.textContent = _account.eMail;

			_accountRecord.addEventListener ('click', event => 
			{
				_foldersDummy.classList.add ('displayNone');
				_browserSectionDummy.classList.remove ('displayNone');
				_letterSectionDummy.classList.remove ('displayNone');

				for (const _record of _accountRecords)
					_record.querySelector ('.controlSectionTokenMarker').textContent = _record === _accountRecord ? '➤' : '';

				_clearElement (_foldersContainer);

				const _folderRecords = [];
				for (const _folder of _account.folders)
				{
					const _folderRecord = _folderRecordTemplate.cloneNode (true);
					_foldersContainer.append (_folderRecord);

					const _name = _folderRecord.querySelector ('.controlSectionTokenName');
					const _count = _folderRecord.querySelector ('.folderCount');
		
					_name.textContent = _folder.name;
					_count.textContent = _folder.count > 0 ? String (_folder.count) : '';

					_folderRecord.addEventListener ('click', event => 
					{
						_browserPageControl.classList.add ('displayNone');
						_browserSectionDummy.classList.add ('displayNone');
						_letterSectionDummy.classList.remove ('displayNone');

						for (const _record of _folderRecords)
							_record.querySelector ('.controlSectionTokenMarker').textContent = _record === _folderRecord ? '➤' : '';
			
						_browserSectionTitle.textContent = _folder.name;

						_loadFolder (_account.eMail, _folder.name, 0, _lettersPerPage - 1, folderData =>	
						{
							if (folderData.letters.length >= _lettersPerPage)
							{
								_browserPageControl.classList.remove ('displayNone');

								_current.folderPagesCount = Math.floor (folderData.lettersTotalCount / _lettersPerPage) + (folderData.lettersTotalCount % _lettersPerPage !== 0);

								_browserPageInput.value = '1';
								_browserSectionPageCount.textContent = _current.folderPagesCount;
							};

							_displayFolderRecords (folderData.letters);
						});

						_current.folderName = _folder.name;
						_current.letter = null;
					});

					_folderRecords.push (_folderRecord);
				};

				_current.accountEmail = _account.eMail;
				_current.folderName = null;
				_current.folderPagesCount = null;
				_current.letter = null;
			});

			_accountRecords.push (_accountRecord);
		};
	};

	const _displayFolderRecords = folderLettersArray =>
	{
		_clearElement (_browserRecordsContainer);

		_browserRecordsContainer.scrollTop = 0;

		for (const _letter of folderLettersArray)
		{
			const _browserRecord = _browserRecordTemplate.cloneNode (true);
			_browserRecordsContainer.append (_browserRecord);

			const _logo = _browserRecord.querySelector ('.browserRecordLogo');
			const _sender = _browserRecord.querySelector ('.browserRecordSender');
			const _title = _browserRecord.querySelector ('.browserRecordTitle');
			const _text = _browserRecord.querySelector ('.browserRecordLetter');
			const _date = _browserRecord.querySelector ('.browserRecordDate');

			let _logoText = '';
			const _browserRecordTitleWords = _letter.senderName.split (' ');
			for (let j = 0; j < Math.min (_browserRecordTitleWords.length, 2); j ++)
				_logoText += _browserRecordTitleWords [j] [0];

			let _html;
			const _bodyOpenTagIndex = _letter.html.indexOf ('<body');
			if (_bodyOpenTagIndex !== - 1)
				_html = _letter.html.slice (_letter.html.indexOf ('>', _bodyOpenTagIndex) + 1, _letter.html.indexOf ('</body>'));
			else
				_html = _letter.html;

			const _div = document.createElement ('div');
			_div.innerHTML = _html;

			_logo.textContent = _logoText;
			_sender.textContent = _letter.senderName;
			_title.textContent = _letter.title;
			_text.textContent = _div.textContent;
			_date.innerHTML = _letter.date + '<br>' + _letter.time;

			_browserRecord.addEventListener ('click', event =>
			{
				const _browserRecords = Array.from (_browserRecordsContainer.querySelectorAll ('.browserRecord'));
				for (const _record of _browserRecords)
					if (_record === _browserRecord)
						_record.classList.add ('browserRecordSelected');
					else
						_record.classList.remove ('browserRecordSelected');

				_letterWriteBlock.classList.add ('displayNone');
				_letterSectionDummy.classList.add ('displayNone');

				_letterReadBlockTitle.textContent = _letter.title;
				_letterReadBlockSenderName.textContent = _letter.senderName;
				_letterReadBlockSenderEmail.textContent = _letter.senderEmail;
				_letterReadBlockRecipientName.textContent = _letter.recipientName;
				_letterReadBlockRecipientEmail.textContent =  _letter.recipientEmail;
				_letterReadBlockIframe.srcdoc = _letter.html;

				_current.letter = _letter;
				_current.letter.text = _text.textContent;
			});
		};
	};

	_loadAccounts (_displayAccounts);

	_writeButton.addEventListener ('click', event =>
	{
		if (_current.accountEmail !== null)
		{
			_letterWriteBlock.classList.remove ('displayNone');
			_letterWriteQuote.classList.add ('displayNone');
			_letterSectionDummy.classList.add ('displayNone');

			_letterWriteFromEmail.value = _current.accountEmail;
			_letterWriteTo.value = '';
			_letterWriteTitle.value = '';
			_letterWriteTextarea.value = '';
		};
	});

	_replyButton.addEventListener ('click', event =>
	{
		if (_current.letter !== null && _current.letter.senderEmail !== _current.accountEmail)
		{
			_letterWriteBlock.classList.remove ('displayNone');
			_letterWriteQuote.classList.remove ('displayNone');
			_letterSectionDummy.classList.add ('displayNone');

			_letterWriteFromEmail.value = _current.accountEmail;
			_letterWriteTo.value = _current.letter.senderEmail;
			_letterWriteTitle.value = 'Re: ' + _current.letter.title;
			_letterWriteQuote.textContent = _current.letter.text;
			_letterWriteTextarea.value = '';
		};
	});

	_addAccountButton.addEventListener ('click', event => _addAccountPopup.classList.remove ('displayNone'));

	_addAccountPopupCloseButton.addEventListener ('click', event => _addAccountPopup.classList.add ('displayNone'));

	_browserPageInput.addEventListener ('input', event =>
	{
		let _pageIndex = Number (_browserPageInput.value) - 1;
		if (_browserPageInput.value !== '' && ! Number.isNaN (_pageIndex))
		{
			_letterSectionDummy.classList.remove ('displayNone');

			_pageIndex = Math.max (Math.min (_pageIndex, _current.folderPagesCount - 1), 0);

			_browserPageInput.value = _pageIndex + 1;

			_loadFolder (_current.accountEmail, _current.folderName, _pageIndex * _lettersPerPage, (_pageIndex + 1) * _lettersPerPage - 1, folderData => _displayFolderRecords (folderData.letters));

			_current.letter = null;
		};
	});

	_browserPageInput.addEventListener ('blur', event =>
	{
		let _pageIndex = Number (_browserPageInput.value) - 1;
		if (_browserPageInput.value === '' || Number.isNaN (_pageIndex))
		{
			_letterSectionDummy.classList.remove ('displayNone');

			_browserPageInput.value = 1;

			_loadFolder (_current.accountEmail, _current.folderName, 0, _lettersPerPage - 1, folderData => _displayFolderRecords (folderData.letters));

			_current.letter = null;
		};
	});
});

/*

url: 'http://localhost:8080/getAccounts'
method: 'GET'
return:
[
	{
		eMail: 'nikita-romanov71@rambler.ru',
		folders:
		[
			{
				name: 'Входящие',
				count: 52,
			},
			...
		],
	},
	...
];

-----------------------------------------------------------

url: 'http://localhost:8080/getFolder?accountEmail=...&folderName=...&fromIndex=10&toIndex=45'
method: 'GET'
return:
{	
	lettersTotalCount: 500,
	letters:
	[
		{
			date: '12.12.2012',
			time: '22:16',
			senderName: 'Сбербанк',
			senderEmail: 'noreply@quora.com',
			recipientName: 'Nikita Romanov 71',
			recipientEmail: 'nikita.romanov@rambler.ru',
			title: 'Здравствуйте блять! Вам подарок!',
			html: '...',
			seen: true,
			important: false,
			attachments: 
			[
				'file.pdf',
				...
			],
		},
		...
	],
}

-----------------------------------------------------------

url: '/logout'
method: 'POST'

-----------------------------------------------------------

url: '/addAccount?login=...&password=...'
method: 'POST'

-----------------------------------------------------------

url: '/sendLetter?senderEmail=...&recipientEmail=...&title=...'
method: 'POST'
body: 'letterText'

*/