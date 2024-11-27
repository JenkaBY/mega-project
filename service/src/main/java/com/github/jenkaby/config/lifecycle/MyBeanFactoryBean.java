package com.github.jenkaby.config.lifecycle;

import com.github.jenkaby.service.MyBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class MyBeanFactoryBean  implements FactoryBean<MyBean> {

    @Override
    public MyBean getObject() throws Exception {
        System.out.println("[FactoryBean] MyBeanFactoryBean.getObject()");
        MyBean myBean = new MyBean();
        myBean.sendMessage("set Message from FactoryBean");
        return myBean;
    }

    @Override
    public Class<?> getObjectType() {
        return MyBean.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
