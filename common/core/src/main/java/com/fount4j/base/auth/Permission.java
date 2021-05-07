package com.fount4j.base.auth;

public @interface Permission {
    /**
     * 权限代码，推荐使用 grandparent:parent:name-in-kebab-case 的格式
     * <p></p>
     * Permission code, recommended formatting: grandparent:parent:name-in-kebab-case
     *
     * @return 全局唯一的权限代码,
     * <p></p>
     * permission unique code
     */
    String code();

    Type type() default Type.API;

    enum Type {
        /**
         * 当没有权限时，返回错误页面
         * <p></p>
         * Return page view while no permission
         */
        MENU,
        /**
         * 根据 accept 的 contentType 决定返回 json 错误还是 xml 错误
         * <p></p>
         * Return json or xml (based on the accepted content type) error response while no permission
         */
        API,
        /**
         * Will trigger an action (click button)
         */
        ACTION
    }
}
