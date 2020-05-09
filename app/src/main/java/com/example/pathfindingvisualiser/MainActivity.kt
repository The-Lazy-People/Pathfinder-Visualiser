package com.example.pathfindingvisualiser


import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.varunest.sparkbutton.SparkButton
import com.varunest.sparkbutton.SparkButtonBuilder
import com.varunest.sparkbutton.SparkEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val buttonStatusKeeper: MutableList<MutableMap<SparkButton,Int>> = ArrayList()
    val buttons: MutableList<MutableList<SparkButton>> = ArrayList()
    val size=10
    val sizeb=20
    var startStatusKeeper:Int=0
    var endStatusKeeper:Int=0
    var butsrcx:Int=-1
    var butsrcy:Int=-1
    var butdesx:Int=-1
    var butdesy:Int=-1
    var buttonWeightStatus=0

    val gdForRedColor:GradientDrawable= GradientDrawable()
    val gdForGreenColor:GradientDrawable= GradientDrawable()
    val gdForBrownColor:GradientDrawable= GradientDrawable()
    val gdForWhiteColor:GradientDrawable= GradientDrawable()
    val gdForBlueColor:GradientDrawable= GradientDrawable()


    var v:MutableList<MutableList<MutableList<MutableList<Int>>>> = mutableListOf()
    var dis:MutableList<MutableList<Int>> = mutableListOf()
    var path:MutableList<MutableList<MutableList<MutableList<Int>>>> = mutableListOf()
    @RequiresApi(Build.VERSION_CODES.N)
    var pq: PriorityQueue<Tuple2> = PriorityQueue<Tuple2>(ComparatorTuple)
    var weight:MutableList<MutableList<Int>> = mutableListOf()
    var sized: Int=0
    var srcx: Int=0
    var srcy: Int=0
    var desx: Int=-1
    var desy: Int=-1

    var vis:MutableList<MutableList<Int>> = mutableListOf()
    var dfsPath:MutableList<MutableList<Int>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gradientDrawableValueSetter()
        createButtonGrid()
        //paintAllButtonsWhite()
        search.setOnClickListener {
            if(startStatusKeeper==0)
                Toast.makeText(this,"Select Starting Node!!",Toast.LENGTH_LONG).show()
            else if(endStatusKeeper==0)
                Toast.makeText(this,"Select Ending Node!!",Toast.LENGTH_LONG).show()
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    findPathdijikstra()
                }
            }
        }
        weightbut.setOnClickListener {
            if(buttonWeightStatus==0) {
                weightbut.text="BLOCK"
                buttonWeightStatus = 1
            }
            else {
                weightbut.text="WEIGHT"
                buttonWeightStatus = 0
            }
        }
        clearbut.setOnClickListener {
            clearGrid()
        }


        mazebut.setOnClickListener {
            if(startStatusKeeper==0) {
                clearGrid()
                createmaze()
            }
            else
                Toast.makeText(this,"Maze can only be generated in begining",Toast.LENGTH_LONG).show()
        }
    }

    private fun createmaze() {
        for (k in 0..100) {
            var i = (0..sizeb).random()
            var j = (0..size).random()
            buttonStatusKeeper[i].put(buttons[i][j],1)
            buttons[i][j].setInactiveImage(R.drawable.ic_mathematics)
            buttons[i][j].setActiveImage(R.drawable.ic_mathematics)
            buttons[i][j].playAnimation()
        }
    }

    private fun clearGrid() {
        var screenid = resources.getIdentifier("screen", "id", packageName)
        val screen=findViewById<LinearLayout>(screenid)
        (screen.getParent() as ViewGroup).removeView(screen)
        buttons.removeAll(buttons)
        buttonStatusKeeper.removeAll(buttonStatusKeeper)
        startStatusKeeper=0
        endStatusKeeper=0
        butsrcx=-1
        butsrcy=-1
        butdesx=-1
        butdesy=-1
        buttonWeightStatus=0
        v.removeAll(v)
        dis.removeAll(dis)
        path.removeAll(path)
        weight.removeAll(weight)
        sized=0
        srcx=0
        srcy=0
        desx=-1
        desy=-1
        createButtonGrid()
        vis.removeAll(vis)
        dfsPath.removeAll(dfsPath)
        search.isClickable=true
        weightbut.isClickable=true

    }
    suspend fun dfs(x:Int, y:Int):Boolean{
        if(vis[x][y]==0)
        {
            buttons[x][y].setInactiveImage(R.drawable.ic_mathematics_blue)
            buttons[x][y].playAnimation()
            delay(50)
            vis[x][y]=1
            if(x==desx){
                if(y==desy){
                    var point:MutableList<Int> = mutableListOf()
                    point.add(x)
                    point.add(y)
                    dfsPath.add(point)
                    return true
                }
            }
            for (i in 0..(v[x][y].size-1)){
                var returner_val=false
                var job1=GlobalScope.launch(Dispatchers.Main) {
                    returner_val = dfs(v[x][y][i][0], v[x][y][i][1])
                }
                job1.join()
                if(returner_val==true){
                    var point:MutableList<Int> = mutableListOf()
                    point.add(x)
                    point.add(y)
                    dfsPath.add(point)
                    return true
                }
            }
        }
        return false
    }
    suspend fun findPathDFS(){
        search.isClickable=false
        clearbut.isClickable=false
        for (i in 0..(sizeb)) {
            var row: MutableList<MutableList<MutableList<Int>>> = mutableListOf()
            for (j in 0..(size)) {
                var point: MutableList<MutableList<Int>> = mutableListOf()
                if (i == 0 && j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)

                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j + 1)

                    point.add(neigh2)

                } else if (i == sizeb  && j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j + 1)

                    point.add(neigh2)

                } else if (i == 0 && j == size) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)

                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j - 1)

                    point.add(neigh2)

                } else if (i == sizeb  && j == size ) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)

                    point.add(neigh2)

                } else if (i == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)

                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)

                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)

                    point.add(neigh3)

                } else if (i == sizeb) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)

                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)

                    point.add(neigh3)

                } else if (j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i + 1)
                    neigh2.add(j)

                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)

                    point.add(neigh3)

                } else if (j == size) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i + 1)
                    neigh2.add(j)

                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j - 1)

                    point.add(neigh3)

                } else {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)

                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i + 1)
                    neigh2.add(j)

                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j - 1)
                    point.add(neigh3)
                    var neigh4: MutableList<Int> = mutableListOf()

                    neigh4.add(i)
                    neigh4.add(j + 1)

                    point.add(neigh4)

                }
                row.add(point)
            }
            v.add(row)
        }

        for (i in 0..(v.size - 1)) {
            var visvec: MutableList<Int> = mutableListOf()
            for (j in 0..(v[i].size - 1)) {

                visvec.add(0)
            }
            vis.add(visvec)
        }

        for (i in 0..sizeb){
            for(j in 0..size){
                if(buttonStatusKeeper[i].get(buttons[i][j])==1){
                    vis[i][j]=1
                }
            }
        }


        srcx=butsrcx
        srcy=butsrcy
        desx=butdesx
        desy=butdesy
        var job2=GlobalScope.launch(Dispatchers.Main) {
        dfs(srcx,srcy)}
        job2.join()

        for (i in (dfsPath.size-2) downTo 1){
            buttons[dfsPath[i][0]][dfsPath[i][1]].setInactiveImage(R.drawable.ic_mathematics_green)
            buttons[dfsPath[i][0]][dfsPath[i][1]].setActiveImage(R.drawable.ic_mathematics_green)
            buttons[dfsPath[i][0]][dfsPath[i][1]].playAnimation()
            delay(100)

        }
        if(dfsPath.size==0)
        {
            Toast.makeText(this,"NO PATH FOUND!!",Toast.LENGTH_LONG).show()
        }
        clearbut.isClickable=true
    }
    fun weightMaker() {
        for (i in 0..(sizeb)) {
            var weightvec: MutableList<Int> = mutableListOf()
            for (j in 0..(size)) {
                weightvec.add(1)
            }
            weight.add(weightvec)
        }
    }
    suspend fun mainer() {
        //buttons[0][0].setBackgroundColor(Color.parseColor("#000000"))
        for (i in 0..(sizeb)) {
            var row: MutableList<MutableList<MutableList<Int>>> = mutableListOf()
            for (j in 0..(size)) {
                var point: MutableList<MutableList<Int>> = mutableListOf()
                if (i == 0 && j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)
                    neigh1.add(weight[i + 1][j])
                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j + 1)
                    neigh2.add(weight[i][j + 1])
                    point.add(neigh2)

                } else if (i == sizeb  && j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j + 1)
                    neigh2.add(weight[i][j + 1])
                    point.add(neigh2)

                } else if (i == 0 && j == size) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)
                    neigh1.add(weight[i + 1][j])
                    point.add(neigh1)

                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i)
                    neigh2.add(j - 1)
                    neigh2.add(weight[i][j - 1])
                    point.add(neigh2)

                } else if (i == sizeb  && j == size ) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)
                    neigh2.add(weight[i][j - 1])
                    point.add(neigh2)

                } else if (i == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i + 1)
                    neigh1.add(j)
                    neigh1.add(weight[i + 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)
                    neigh2.add(weight[i][j - 1])
                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)
                    neigh3.add(weight[i][j + 1])
                    point.add(neigh3)

                } else if (i == sizeb) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i)
                    neigh2.add(j - 1)
                    neigh2.add(weight[i][j - 1])
                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)
                    neigh3.add(weight[i][j + 1])
                    point.add(neigh3)

                } else if (j == 0) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()
                    neigh2.add(i + 1)
                    neigh2.add(j)
                    neigh2.add(weight[i + 1][j])
                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j + 1)
                    neigh3.add(weight[i][j + 1])
                    point.add(neigh3)

                } else if (j == size) {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i + 1)
                    neigh2.add(j)
                    neigh2.add(weight[i + 1][j])
                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j - 1)
                    neigh3.add(weight[i][j - 1])
                    point.add(neigh3)

                } else {
                    var neigh1: MutableList<Int> = mutableListOf()
                    neigh1.add(i - 1)
                    neigh1.add(j)
                    neigh1.add(weight[i - 1][j])
                    point.add(neigh1)
                    var neigh2: MutableList<Int> = mutableListOf()

                    neigh2.add(i + 1)
                    neigh2.add(j)
                    neigh2.add(weight[i + 1][j])
                    point.add(neigh2)
                    var neigh3: MutableList<Int> = mutableListOf()

                    neigh3.add(i)
                    neigh3.add(j - 1)
                    neigh3.add(weight[i][j - 1])
                    point.add(neigh3)
                    var neigh4: MutableList<Int> = mutableListOf()

                    neigh4.add(i)
                    neigh4.add(j + 1)
                    neigh4.add(weight[i][j + 1])
                    point.add(neigh4)

                }
                row.add(point)
            }
            v.add(row)
        }


        for (i in 0..(v.size - 1)) {
            var row: MutableList<MutableList<MutableList<Int>>> = mutableListOf()
            for (j in 0..(v[i].size - 1)) {
                var p: MutableList<MutableList<Int>> = mutableListOf()
                row.add(p)
            }
            path.add(row)
        }

        for (i in 0..(v.size - 1)) {
            var disvec: MutableList<Int> = mutableListOf()
            for (j in 0..(v[i].size - 1)) {

                disvec.add(500)
            }
            dis.add(disvec)
        }


        var temp = Tuple2(0, srcx, srcy)
        pq.add(temp)
        dis[srcx][srcy] = 0
        while (!pq.isEmpty()) {
            var u = pq.peek()
            pq.remove()
            var x = u.x
            var y = u.y
            var d = u.d
            //tester.append(x.toString()+" "+y.toString()+"\n")
            if ((x == desx) and (y == desy)) {
                break
            }
            for (i in 0..(v[x][y].size -1)) {

                if (dis[v[x][y][i][0]][v[x][y][i][1]] > ((dis[x][y]) + (v[x][y][i][2]))) {
                    if ((v[x][y][i][0] != desx) or (v[x][y][i][1] != desy)) {
                        if (weight[v[x][y][i][0]][v[x][y][i][1]] == 1) {
                            buttons[v[x][y][i][0]][v[x][y][i][1]].setInactiveImage(R.drawable.ic_mathematics_blue)
                            buttons[v[x][y][i][0]][v[x][y][i][1]].playAnimation()
                            delay(50)
                        }

                    }
                    dis[v[x][y][i][0]][v[x][y][i][1]] = ((dis[x][y]) + (v[x][y][i][2]))

                    path[v[x][y][i][0]][v[x][y][i][1]].removeAll(path[v[x][y][i][0]][v[x][y][i][1]])

                    path[v[x][y][i][0]][v[x][y][i][1]] =
                        mutableListOf<MutableList<Int>>().apply { addAll(path[x][y]) }
                    var tem: MutableList<Int> = mutableListOf()
                    tem.add(x)
                    tem.add(y)
                    path[v[x][y][i][0]][v[x][y][i][1]].add(tem)
                    var dd: Int = dis[v[x][y][i][0]][v[x][y][i][1]]
                    var xx: Int = v[x][y][i][0]
                    var yy: Int = v[x][y][i][1]
                    var temp2 = Tuple2(dd, xx, yy)
                    pq.add(temp2)
                }
            }
        }
    }
    private suspend fun findPathdijikstra() {
        search.isClickable=false
        weightbut.isClickable=false
        clearbut.isClickable=false
        sized=size+1
        srcx=butsrcx
        srcy=butsrcy
        desx=butdesx
        desy=butdesy
        var job2=GlobalScope.launch(Dispatchers.Main) {
        weightMaker()}
        job2.join()
        for (i in 0..sizeb){
            for(j in 0..size){
                if(buttonStatusKeeper[i].get(buttons[i][j])==1){
                    weight[i][j]=1000
                }
                else if(buttonStatusKeeper[i].get(buttons[i][j])==2){
                    weight[i][j]=5
                }
            }
        }
        var job1=GlobalScope.launch(Dispatchers.Main) {
        mainer()}
        job1.join()
        var pather=path
        //tester.append(srcx.toString()+" "+srcy.toString()+butsrcx.toString()+" "+butsrcy.toString()+"\n")
        //tester.append(desx.toString()+" "+desy.toString()+butdesx.toString()+" "+butdesy.toString()+"\n")
        for (i in 1..(pather[butdesx][butdesy].size-1)){
            buttons[pather[butdesx][butdesy][i][0]][pather[butdesx][butdesy][i][1]].setInactiveImage(R.drawable.ic_mathematics_green)
            buttons[pather[butdesx][butdesy][i][0]][pather[butdesx][butdesy][i][1]].setActiveImage(R.drawable.ic_mathematics_green)
            buttons[pather[butdesx][butdesy][i][0]][pather[butdesx][butdesy][i][1]].playAnimation()
            delay(200)

            //tester.append(pather[butdesx][butdesy][i][0].toString()+" "+pather[butdesx][butdesy][i][1].toString()+"\n")

        }
        if(pather[butdesx][butdesy].size==0){
            Toast.makeText(this,"NO PATH FOUND", Toast.LENGTH_LONG).show()
        }
        clearbut.isClickable=true
    }
    private fun gradientDrawableValueSetter() {
        gdForRedColor.setColor(Color.parseColor("#FF0000"))
        gdForRedColor.cornerRadius=10.0f
        gdForRedColor.setStroke(1,Color.parseColor("#000000"))

        gdForBrownColor.setColor(Color.parseColor("#A52A2A"))
        gdForBrownColor.cornerRadius=10.0f
        gdForBrownColor.setStroke(1,Color.parseColor("#000000"))

        gdForGreenColor.setColor(Color.parseColor("#008000"))
        gdForGreenColor.cornerRadius=10.0f
        gdForGreenColor.setStroke(1,Color.parseColor("#000000"))

        gdForWhiteColor.setColor(Color.parseColor("#FFFFFF"))
        gdForWhiteColor.cornerRadius=10.0f
        gdForWhiteColor.setStroke(1,Color.parseColor("#000000"))

        gdForBlueColor.setColor(Color.parseColor("#0000FF"))
        gdForBlueColor.cornerRadius=10.0f
        gdForBlueColor.setStroke(1,Color.parseColor("#000000"))
    }
    private fun createButtonGrid() {
        val screenLinearLayout = LinearLayout(this)
        screenLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        screenLinearLayout.orientation = LinearLayout.VERTICAL
        var screenid = resources.getIdentifier("screen", "id", packageName)
        screenLinearLayout.id=screenid
        mainscreen.addView(screenLinearLayout)
        for (i in 0..sizeb) {

            val arrayLinearLayout = LinearLayout(this)
            arrayLinearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,1.0f
            )
            arrayLinearLayout.orientation = LinearLayout.HORIZONTAL
            //arrayLinearLayout.setPadding(2,2,2,2)

            val buttonStatusRow: MutableMap<SparkButton,Int> = mutableMapOf()
            val buttonRow:MutableList<SparkButton> = mutableListOf()
            for (j in 0..(size)) {
                val sbutton: SparkButton = SparkButtonBuilder(this).setImageSizeDp(30)
                    .setActiveImage(R.drawable.ic_mathematics)
                    .setInactiveImage(R.drawable.ic_mathematics_empty)
                    .setPrimaryColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
                    .setSecondaryColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    .build()
                sbutton.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f
                )

                sbutton.setEventListener(object : SparkEventListener {
                    override fun onEventAnimationEnd(button: ImageView?, buttonState: Boolean) {

                    }

                    override fun onEvent(button: ImageView?, buttonState: Boolean) {
                        if(startStatusKeeper==0)
                        {
                            sbutton.setActiveImage(R.drawable.ic_trending_flat_24px)
                            sbutton.isClickable=false
                            sbutton.setInactiveImage(R.drawable.ic_trending_flat_24px)
                            startStatusKeeper=1
                            buttonStatusRow.put(sbutton, 3)
                            Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                            butsrcx=i
                            butsrcy=j

                        }
                        else if(endStatusKeeper==0){
                            sbutton.setActiveImage(R.drawable.ic_gps_fixed_24px)
                            sbutton.isClickable=false
                            sbutton.setInactiveImage(R.drawable.ic_gps_fixed_24px)
                            //sbutton.pressOnTouch(false)
                            endStatusKeeper=1
                            buttonStatusRow.put(sbutton, 3)
                            Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                            butdesx=i
                            butdesy=j

                            //findPath()
                        }
                        else {
                            if(buttonStatusRow.get(sbutton)!=3) {
                                if (buttonWeightStatus == 0) {
                                    sbutton.setActiveImage(R.drawable.ic_mathematics)
                                    val buttonStatus = buttonStatusRow.get(sbutton)
                                    if (buttonStatus == 0) {
                                        buttonStatusRow.put(sbutton, 1)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    } else if (buttonStatus == 1) {
                                        buttonStatusRow.put(sbutton, 0)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    } else {
                                        buttonStatusRow.put(sbutton, 0)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    }
                                } else {
                                    sbutton.setActiveImage(R.drawable.ic_gymnastic)
                                    val buttonStatus = buttonStatusRow.get(sbutton)
                                    if (buttonStatus == 0) {
                                        buttonStatusRow.put(sbutton, 2)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    } else if (buttonStatus == 2) {
                                        buttonStatusRow.put(sbutton, 0)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    } else {
                                        buttonStatusRow.put(sbutton, 0)
                                        Log.i("buttonstatus","BUTTON "+i.toString()+" "+j.toString()+"="+buttonStatusRow.get(sbutton).toString())
                                    }
                                }
                            }
                        }
                    }

                    override fun onEventAnimationStart(button: ImageView?, buttonState: Boolean) {

                    }
                })

//

                buttonStatusRow.put(sbutton,0)
                buttonRow.add(sbutton)
                arrayLinearLayout.addView(sbutton)
            }
            buttonStatusKeeper.add(buttonStatusRow)
            buttons.add(buttonRow)
            screenLinearLayout.addView(arrayLinearLayout)
        }
    }
}











