const _CSRF_META = document.querySelector('meta[name="_csrf"]');
if (_CSRF_META) {
  _CSRF_TOKEN = _CSRF_META.getAttribute('content')
}
const _CSRF_HEADER_META = document.querySelector('meta[name="_csrf_header"]');
if (_CSRF_HEADER_META) {
  _CSRF_HEADER = _CSRF_HEADER_META.getAttribute('content')
}
const _ERROR_MESSAGE = $('meta[name="_error_message"]').attr('content');
$(function () {
  if (_ERROR_MESSAGE) {
    $('body').toast({
      message: _ERROR_MESSAGE,
      displayTime: 0,
      position: 'top center',
      class: 'error',
      showIcon: true
    });
  }
});
