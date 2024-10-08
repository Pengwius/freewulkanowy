package io.github.freewulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.freewulkanowy.R
import io.github.freewulkanowy.data.pojos.RegisterUser
import io.github.freewulkanowy.databinding.ActivityLoginBinding
import io.github.freewulkanowy.ui.base.BaseActivity
import io.github.freewulkanowy.ui.modules.login.advanced.LoginAdvancedFragment
import io.github.freewulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.freewulkanowy.ui.modules.login.onboarding.LoginOnboardingWarningFragment
import io.github.freewulkanowy.ui.modules.login.onboarding.LoginOnboardingWelcomeFragment
import io.github.freewulkanowy.ui.modules.login.recover.LoginRecoverFragment
import io.github.freewulkanowy.ui.modules.login.studentselect.LoginStudentSelectFragment
import io.github.freewulkanowy.ui.modules.login.symbol.LoginSymbolFragment
import io.github.freewulkanowy.ui.modules.main.MainActivity
import io.github.freewulkanowy.ui.modules.notifications.NotificationsFragment
import io.github.freewulkanowy.utils.AppInfo
import io.github.freewulkanowy.utils.InAppUpdateHelper
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginPresenter, ActivityLoginBinding>(), LoginView {

    @Inject
    override lateinit var presenter: LoginPresenter

    @Inject
    lateinit var inAppUpdateHelper: InAppUpdateHelper

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityLoginBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.loginToolbar)
        messageContainer = binding.loginContainer
        inAppUpdateHelper.messageContainer = binding.loginContainer

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val hasCompletedOnboarding = prefs.getBoolean("completedOnboarding", false)

        presenter.onAttachView(this)
        inAppUpdateHelper.checkAndInstallUpdates()

        if (!hasCompletedOnboarding) {
            openFragment(LoginOnboardingWarningFragment.newInstance(), clearBackStack = true)
        } else if (savedInstanceState == null) {
            navigateToLoginForm()
        }
    }

    override fun initView() {
        with(requireNotNull(supportActionBar)) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun showActionBar(show: Boolean) {
        supportActionBar?.run { if (show) show() else hide() }
    }

    fun navigateToOnboardingWelcomeFragment() {
        openFragment(LoginOnboardingWelcomeFragment.newInstance())
    }

    fun navigateToLoginForm() {
        openFragment(LoginFormFragment.newInstance(), clearBackStack = true)
    }

    fun navigateToSymbolFragment(loginData: LoginData) {
        openFragment(LoginSymbolFragment.newInstance(loginData))
    }

    fun navigateToStudentSelect(loginData: LoginData, registerUser: RegisterUser) {
        openFragment(LoginStudentSelectFragment.newInstance(loginData, registerUser))
    }

    fun navigateToNotifications() {
        val isNotificationsPermissionRequired = appInfo.systemVersion >= TIRAMISU
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, "android.permission.POST_NOTIFICATIONS"
        ) == PackageManager.PERMISSION_GRANTED

        if (isNotificationsPermissionRequired && !isPermissionGranted) {
            openFragment(NotificationsFragment.newInstance(), clearBackStack = true)
        } else navigateToFinish()
    }

    fun navigateToFinish() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }

    fun onAdvancedLoginClick() {
        openFragment(LoginAdvancedFragment.newInstance())
    }

    fun onRecoverClick() {
        openFragment(LoginRecoverFragment.newInstance())
    }

    private fun openFragment(fragment: Fragment, clearBackStack: Boolean = false) {
        if (clearBackStack) {
            supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        } else {
            supportFragmentManager.popBackStack(fragment::class.java.name, POP_BACK_STACK_INCLUSIVE)
        }

        supportFragmentManager.commit {
            replace(R.id.loginContainer, fragment)
            setReorderingAllowed(true)
            if (!clearBackStack) addToBackStack(fragment::class.java.name)
        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdateHelper.onResume()
        presenter.updateSdkMappings()
    }
}
