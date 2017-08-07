package io.github.wulkanowy.activity.dashboard.marks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.wulkanowy.R;
import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.api.grades.Subject;
import io.github.wulkanowy.api.grades.SubjectsList;
import io.github.wulkanowy.database.accounts.Account;
import io.github.wulkanowy.database.accounts.AccountsDatabase;
import io.github.wulkanowy.database.grades.GradesDatabase;
import io.github.wulkanowy.database.subjects.SubjectsDatabase;

public class MarksFragment extends Fragment {

    private ArrayList<String> subjectsName = new ArrayList<>();

    private View view;

    public MarksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_marks, container, false);

        if (subjectsName.size() == 0) {
            new MarksTask(container.getContext()).execute();
        } else if (subjectsName.size() > 1) {
            createGrid();
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }

        return view;
    }

    public void createGrid() {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(view.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        ImageAdapter adapter = new ImageAdapter(view.getContext(), subjectsName);
        recyclerView.setAdapter(adapter);
    }

    public class MarksTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;
        private Map<String, String> loginCookies;

        MarksTask(Context context) {
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String cookiesPath = mContext.getFilesDir().getPath() + "/cookies.txt";

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cookiesPath));
                loginCookies = (Map<String, String>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Cookies cookies = new Cookies();
                cookies.setItems(loginCookies);

                AccountsDatabase accountsDatabase = new AccountsDatabase(mContext);
                accountsDatabase.open();
                Account account = accountsDatabase.getAccount(mContext.getSharedPreferences("LoginData", mContext.MODE_PRIVATE).getLong("isLogin", 0));
                accountsDatabase.close();

                StudentAndParent snp = new StudentAndParent(cookies, account.getCounty());
                SubjectsList subjectsList = new SubjectsList(snp);

                SubjectsDatabase subjectsDatabase = new SubjectsDatabase(mContext);
                subjectsDatabase.open();
                subjectsDatabase.put(subjectsList.getAll());
                List<Subject> subjects = subjectsDatabase.getAllSubjectsNames();
                subjectsDatabase.close();

                for (Subject subject : subjects) {
                    subjectsName.add(subject.getName());
                }

                GradesList gradesList = new GradesList(snp);
                GradesDatabase gradesDatabase = new GradesDatabase(mContext);
                gradesDatabase.open();
                gradesDatabase.put(gradesList.getAll());
                gradesDatabase.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            createGrid();

            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            super.onPostExecute(result);
        }
    }
}
