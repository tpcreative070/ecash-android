package vn.ecpay.ewallet.ui.contact.component;

import dagger.Subcomponent;
import vn.ecpay.ewallet.ui.contact.fragment.FragmentAddContact;
import vn.ecpay.ewallet.ui.contact.module.AddContactModule;

@Subcomponent(modules = AddContactModule.class)
public interface AddContactComponent {
    void inject(FragmentAddContact fragmentAddContact);
}
