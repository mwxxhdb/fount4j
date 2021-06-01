const App = {
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false,
      },
      rules: {
        username: [
          { required: true, message: i18n.get('auth.ui.form.rule.username'), trigger: 'blur' }
        ],
        password: [
          { required: true, message: i18n.get('auth.ui.login.rule.password'), trigger: 'blur' }
        ]
      },
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid, fields) => {
        if (valid) {
          document.forms['loginForm'].submit();
        } else {
          if (fields['username']) {
            this.$refs['username'].focus();
          } else if (fields['password']) {
            this.$refs['password'].focus();
          }
          return false;
        }
      });
    }
  }
};
const app = Vue.createApp(App);
app.use(ElementPlus);
app.mount("#app");