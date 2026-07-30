function ReadCookie(Name) {
    return document.cookie
        .split('; ')
        .find(Item => Item.startsWith(`${Name}=`))
        ?.split('=')
        .slice(1)
        .join('=') || '';
}

function GetCsrfToken() {
    const Token = ReadCookie('XSRF-TOKEN');
    return Token ? decodeURIComponent(Token) : '';
}

function BuildCsrfHeader() {
    const Token = GetCsrfToken();
    return Token ? { 'X-XSRF-TOKEN': Token } : {};
}

function FillCsrfForms() {
    document.querySelectorAll('input[data-csrf-token]').forEach(Input => {
        Input.value = GetCsrfToken();
    });
}

document.addEventListener('DOMContentLoaded', FillCsrfForms);
