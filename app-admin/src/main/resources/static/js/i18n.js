const i18n = {
    get: function (code, ...args) {
        let format = this.messages[code] || code;
        for (let i = 0; i < args.length; i++) {
            format = format.replace('{' + i + '}', args[i]);
        }
        return format;
    },
    messages: {
    }
};