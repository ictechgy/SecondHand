package org.jh.secondhand;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import org.jh.secondhand.data.LoginDataSource;
import org.jh.secondhand.data.LoginRepository;
import org.jh.secondhand.ui.buy.BuyFragment;
import org.jh.secondhand.ui.login.LoginActivity;
import org.jh.secondhand.ui.sell.SellFragment;
import org.jh.secondhand.ui.view.SellViewFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnFragmentInteractionListener, MenuItem.OnMenuItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    FloatingActionButton fab;
    NavController navController;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("onCreate()", "시작됨"); //test용. onAttachFragment()
        Log.d("로그인 상태. Main onCreate()", LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()+"" );


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDestination destination = navController.getCurrentDestination();
                if(destination != null && (destination.getId()==R.id.nav_sell || destination.getId()==R.id.nav_view_sell)){
                    navController.navigate(R.id.nav_write_sell);
                }else if(destination != null && destination.getId()==R.id.nav_buy){
                    //navigate to nav_write_buy fragment screen
                }

            }
        });
        //fab 기능 정의 및 디자인 등. 메터리얼 디자인 및 글쓰는 기능 만들기.

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder( //각각의 drawer를 통해 가게 할 메뉴들을 이곳에 등록. top level로 관리.
                R.id.nav_home, R.id.nav_sell, R.id.nav_buy, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //findViewById(R.id.nav_logout).setOnClickListener(this);     //NullPointerException
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                int destinationID = destination.getId();
                if(destinationID == R.id.nav_sell || destinationID == R.id.nav_buy || destinationID == R.id.nav_view_sell){
                    fab.setVisibility(View.VISIBLE);    //사용자의 화면이 판매글 목록이나 구매글 목록, 판매글 상세보기 화면인 경우 게시글 작성 fab 버튼 보이기
                }else if(fab.getVisibility() == View.VISIBLE){
                    fab.setVisibility(View.INVISIBLE);  //그 외의 화면인 경우 fab 버튼이 보이고 있는 상태라면 보이지 않도록 조정(판매글 작성화면이라던지..)
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            Toast.makeText(this,"로그인이 필요합니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, 101);        //onResume()에서 수행?
        }
        //user가 로그인되어있는 경우 메인페이지를 띄우도록 하고 필요한 절차 수행. updateUI


        //Firebase를 이용하여 로그인여부를 확인하는게 좋을까 아니면 LoginRepository를 이용하는게 좋을까.
        /*
        LoginViewModel viewModel = ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        boolean isUserLoggedIn = viewModel.isUserLoggedIn();        //always false
        if(!isUserLoggedIn){
            Log.d("MainActivity 로그인 여부 확인", isUserLoggedIn+"");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 101);
        }

         */

        /*
        LoginRepository repository = getInstance(new LoginDataSource());        //always false
        if(!repository.isLoggedIn()){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, 101);
        }
         */

        //always false인 이유가 앱의 진입점이 MainActivity여서 그런건가. ViewModel이 LoginActivity에서 생성되는데, Main으로 먼저 진입하는 경우 이 VM이 존재하지 않아서?
        //그러면 앱의 진입점을 LoginActivity로 만들어두고 로그인여부를 최초로 한번씩은 검증하도록 한 뒤에 자동로그인 기능까지 넣으면, 이후의 Activity등에서
        //이 VM을 사용할 수 있지 않을까? 로그인 여부를 검증할 때 계속 getCurrentUser를 안쓰고 ViewModel을 이용해 값을 얻는 방식을 통해서.

    }

    @Override
    public void onClick(View v) {   //SelectFragment에 존재하는 두개의 버튼(판매글 보러가기, 구매글 보러가기) 클릭시 작동하는 callback
        /*
        int id = v.getId();
        Fragment fragment;

        switch (id){
            case (R.id.buttonSell):
                fragment = new SellFragment();
                break;
            case (R.id.buttonBuy):
                fragment = new BuyFragment();
                break;
                default:        //예외상황 클릭시 프래그먼트 변경 취소
                    return;
        }
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();

         */

        //위의 방식은 SelectFragment를 fragmentTransaction.add로 최초로 액티비티 띄워질 시 추가해줘야 하는 방식이나 현재 navController방식 사용하므로 사용하지 않음
        //(자동으로 SelectFragment가 띄워지도록 해둠 - mobile_navigation.xml)

        int id = v.getId();
        switch (id){
            case (R.id.buttonSell):
                navController.navigate(R.id.nav_sell);
                break;
            case (R.id.buttonBuy):
                navController.navigate(R.id.nav_buy);
                break;
            /*
            case (R.id.nav_logout):

                new MaterialAlertDialogBuilder(this).setTitle("로그아웃 하시겠습니까?").setMessage("확인을 누르시면 로그아웃 됩니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(false).create();
                break;
             */
        }
        //fab.setVisibility(View.VISIBLE);        //이 메소드를 onAttachFragment 호출 될 때 넣어놓아도 좋을 듯 하다.
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.nav_logout){
            new MaterialAlertDialogBuilder(this).setTitle("로그아웃 하시겠습니까?").setMessage("확인을 누르시면 로그아웃 됩니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivityForResult(intent, 101);
                            //작동하는걸 onStart()부분과 맞춰야하지 않을까. -> ok. 작동 방식을 어떻게 해야할지 못정하겠네.. 그냥 앱의 진입점을 LoginActivity로 해야하나.. 흠.
                            //그냥 startActivity할지 startActivityForResult할지 등.. 그리고 되돌아와서 뭘 처리해야하나?
                            //그리고 현재 이 방식으로 해두면 BackStack에 MainActivity가 남아있어 뒤로 버튼을 누르면 다시 MainActivity가 잠깐 보이다가 다시 onStart()에 의해
                            //LoginActivity가 띄워지는 것으로 보인다. 뒤로가기 누르면 반복적으로 똑같이 .. 반복됨.
                            drawer.closeDrawer(GravityCompat.START);    //버튼 누른 뒤 drawer 닫기.
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setCancelable(false).show();
        }
        return true;
    }

    /*
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {  //fab이 보이고 안보이고는 모두 이 MainActivity에서 관리한다. fragment가 activity에 붙을 때 호출.
        super.onAttachFragment(fragment);
        Log.d("onAttachFragment", "processing");        //여기까지는 잘 나옴.
        if(fab == null) fab = findViewById(R.id.fab);       //fab을 받아오지 못하는건가??
        Log.e("fab status", fab.toString());        //error
        //현재 확인결과 onCreate() 나오기 전에 onAttachFragment 작동.

        if(fragment instanceof SellFragment || fragment instanceof BuyFragment || fragment instanceof SellViewFragment){
            fab.setVisibility(View.VISIBLE);
        }else{  //fragment instanceof SelectFragment, WriteSellFragment...the fab button must not be visible
            if(fab.getVisibility() == View.VISIBLE) fab.setVisibility(View.INVISIBLE);
        }   //왜 fab이 null인거지? 분명 이 메소드는 lifecycle 상  onCreate()이후에 실행이 될 텐데.
    }
    //현재 fragment를 띄우는 방식이 onCreate()에서 FragmentTransaction을 이용하는 방식이 아니어서 그런가? onCreate()이전에 이게 먼저 작동하나?
    */
    //위의 fab Visibility 조정은 navController에 changelistener를 추가하는 방식으로 변경.



    @Override
    public void onFragmentInteraction(@NonNull Bundle bundle) {

        /*
        NavDestination destination = navController.getCurrentDestination();
        if(destination!=null && destination.getId() == R.id.nav_sell){

        }
        */
        //어느 fragment로부터 요청이 온 것인지는 위의 방법으로도 가능.

        String from = bundle.getString("from"); //Fragment별 특정 코드번호로 구분되도록 해도 좋을 것. static final int ~
        if(from != null && from.equals("SellFragment")){
            //SellViewFragment로 가도록 만들 것인데, SellFragment의 내용을 backStack에 넣어두도록 하면, 나중에 뒤로가기를 누르더라도 목록을 reset시킬 필요가 없게 됨.
            //사용자가 원할 때 목록을 업데이트 되도록 하는 방향으로 구축.
            int position = bundle.getInt("position");
            SellViewFragment.newInstance(position);     //이렇게 먼저 인자 넘겨주는 식으로 해당 Fragment 생성시켜주면 되나?
            navController.navigate(R.id.nav_view_sell);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //do something?
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("로그인 상태 Main onResume()", LoginRepository.getInstance(new LoginDataSource()).isLoggedIn()+"" );
        //처음 로그인 화면에서 여기로 로그인 해서 넘어오면 해당 LoginRepository의 로그인 정보를 계속 이용 가능한데, 이미 로그인 하고 앱 다시 띄울 때는 메모리에 해당
        //LoginRepository가 사라져있는 상태여서 그런건지 false로 뜬다 계속. 캐시나 어디에 저장시키도록 해야하는건지.. 어떻게 이용해야하지. 이미 로그인 되어있는 상태이긴 한데.
        //LoginRepository를 계속 쓸 수 있으면 훨씬 효율적일 것 같은데.. 역시나 앱의 진입점을 LoginActivity로?
        //그리고 로그인 시도할 때 비동기 처리구조상 처음 몇번은 계속 Login fail 뜬다.. -> 해결.
    }
}
