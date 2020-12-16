package cga.exercise.game

import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.Material
import cga.exercise.components.geometry.Mesh
import cga.exercise.components.geometry.Renderable
import cga.exercise.components.geometry.VertexAttribute
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.SpotLight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Math
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.opengl.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*




/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {

    val staticShader: ShaderProgram


    var offset: Vector3f?
    var rotate: Vector3f?
    var scale: Vector3f?
    var color: Vector3f?
    var renderObject: Renderable? = null
    var passedTime = 0.0f



    var bodenr: Renderable

    var camera: TronCamera
    var cubeCamera: TronCamera

    var material: Material
    var diff: Texture2D
    var emit: Texture2D
    var spec: Texture2D
    var pointlight: PointLight
    var spotligt: SpotLight

    //Elemente
    var cup: Renderable
    var cube: Renderable
    var tisch: Renderable


    var shadowTexture: Int

    var cam: Int = 0

    var xPosition: Double = 0.0
    var yPosition: Double = 0.0

    //scene setup
    init {

        val window = window
        color = Vector3f(0.0f)
        offset = Vector3f(0.0f)
        rotate = Vector3f(0.0f)
        scale = Vector3f(0.5f)


            //Shader
            // staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
            staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
            //staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")


            //cube würfel
            cube = ModelLoader.loadModel("assets/models/cube.obj", Math.toRadians(0f), Math.toRadians(90f), 0f)
                ?: throw IllegalArgumentException("Could not load the model cube")
            cube.translateGlobal(Vector3f(1.5f, 0f, 3.2f))
            cube.scaleLocal(Vector3f(0.005f, 0.005f, 0.005f))


            //cup tasse
            cup = ModelLoader.loadModel("assets/models/cup.obj", Math.toRadians(0f), Math.toRadians(0f), 0f)
                ?: throw IllegalArgumentException("Could not load the model Cup")
            cup.translateGlobal(Vector3f(-4f, 1.3f, -2.8f))
            cup.scaleLocal(Vector3f(0.1f, 0.1f, 0.1f))


            //tisch
            tisch = ModelLoader.loadModel("assets/models/tisch.obj", Math.toRadians(0f), Math.toRadians(90f), 0f)
                ?: throw IllegalArgumentException("Could not load the model tisch")
            tisch.translateGlobal(Vector3f(-4f, 0f, -3f))
            tisch.scaleLocal(Vector3f(0.03f, 0.03f, 0.03f))


            //
            val res2: OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
            val objMesh2: OBJLoader.OBJMesh = res2.objects[0].meshes[0]

            //Boden
            diff = Texture2D.invoke("assets/textures/ground_diff.png", true)
            diff.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
            emit = Texture2D.invoke("assets/textures/ground_emit.png", true)
            emit.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
            spec = Texture2D.invoke("assets/textures/ground_spec.png", true)
            spec.setTexParams(GL_REPEAT, GL_REPEAT, GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)


            material = Material(diff, emit, spec, 60f, Vector2f(64f, 64f))

            val stride: Int = 8 * 4
            val attrPos = VertexAttribute(3, GL_FLOAT, stride, 0)
            val attrTC = VertexAttribute(2, GL_FLOAT, stride, 3 * 4)
            val attrNorm = VertexAttribute(3, GL_FLOAT, stride, 5 * 4)
            val vertexAttributes = arrayOf(attrPos, attrTC, attrNorm)
            //kugelMesh = Mesh(objMesh.vertexData, objMesh.indexData, vertexAttributes)
            val bodenmesh = Mesh(objMesh2.vertexData, objMesh2.indexData, vertexAttributes, material)


            bodenr = Renderable(mutableListOf(bodenmesh))
            //kugelr = Renderable(mutableListOf(kugelMesh))


            //bodenr.rotateLocal(Math.toRadians(90.0f),0f,0f)
            //bodenr.scaleLocal(Vector3f(0.03f,0.03f,0.03f))
            // kugelr.scaleLocal(Vector3f(0.5f,0.5f,0.5f))

            //Kamera
            camera = TronCamera()
            camera.rotateLocal(0f, 0f, 0f)
            camera.translateLocal(Vector3f(0f, 0f, 40f))


            cubeCamera = TronCamera()
            cubeCamera.translateLocal(Vector3f(0f, 0f, 60f))
             camera.parent = cube

            //Licht (Position, Farbe)
            pointlight = PointLight(Vector3f(0f, 2f, 0f), Vector3f(0f, 1f, 0f))
            pointlight.parent = cube



            spotligt = SpotLight(Vector3f(0f,0f,0f),Vector3f(1f,0f,1f),Math.cos(Math.toRadians(30f)),Math.cos(Math.toRadians(50f)))
            spotligt.parent = cube


            glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
            glEnable(GL_DEPTH_TEST); GLError.checkThrow()
            glDepthFunc(GL_LESS); GLError.checkThrow()


            glEnable(GL_CULL_FACE)
            glFrontFace(GL_CCW)
            glCullFace(GL_BACK)


            shadowTexture = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, shadowTexture)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL_DEPTH_COMPONENT,
                1024,
                1024,
                0,
                GL11.GL_DEPTH_COMPONENT,
                GL11.GL_FLOAT,
                0
            )
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)
            GL11.glTexParameterfv(GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f))

        }



    fun render(dt: Float, t: Float) {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        staticShader.use()
        camera.bind(staticShader)
        staticShader.setUniform("shadingcolor",Vector3f(1f,0f,0f))
        pointlight.bind(staticShader,"bike")
        spotligt.bind(staticShader," ", camera.getCalculateViewMatrix())
        staticShader.setUniform("shadingcolor",Vector3f(0f,1f,0f))
        bodenr.render(staticShader)

        cube.render(staticShader)
        cup.render(staticShader)
        tisch.render(staticShader)

       // GL30.glBindVertexArray(0)
        //GLError.checkExit()



    }

    fun update(dt: Float, t: Float) {


        // für die automatische bewegung des affens
        passedTime++
        offset!!.x += 0.001f
        rotate!!.y += 0.01f
        rotate!!.z += 0.01f
        scale!!.x += 0.001f
        scale!!.y += 0.001f

        //renderObject.translateOBJ(offset)
        //renderObject.rotateOBJ(rotate)
        //renderObject.scaleOBJ(scale)

        renderObject!!.translateLocal(Vector3f(0.0f,0.0f,-5.0f*dt))
        renderObject!!.rotateLocal(0.0f,-2f*dt,0.0f)
       // renderObject!!.scaleLocal(0.0f)

        //Tasten

        //vorwärts
        if(window.getKeyState(GLFW_KEY_W)){

            //cycle.translateLocal(Vector3f(0.0f,0.0f,-5.0f*dt))
            cube.translateLocal(Vector3f(0.0f,0.0f,-5.0f*dt))
        }

        //rückwärts
        if(window.getKeyState(GLFW_KEY_S)){
            //cycle.translateLocal(Vector3f(0f,0f,5.0f*dt))
            cube.translateLocal(Vector3f(0f,0f,5.0f*dt))

        }

        //links
        if(window.getKeyState(GLFW_KEY_A)){
            //cycle.translateLocal(Vector3f(0.0f,0.0f,-5.0f*dt))
            //cycle.rotateLocal(0.0f,1f*dt,0.0f)
            cube.translateLocal(Vector3f(0.0f,0.0f,0f*dt))
            cube.rotateLocal(0.0f,2f*dt,0.0f)
        }

        //rechts
        if(window.getKeyState(GLFW_KEY_D)){

            //cycle.translateLocal(Vector3f(0.0f,0.0f,-5.0f*dt))
            //cycle.rotateLocal(0.0f,-1f*dt,0.0f)
            cube.translateLocal(Vector3f(0.0f,0.0f,0f*dt))
            cube.rotateLocal(0.0f,-2f*dt,0.0f)

        }




    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}




    fun onMouseMove(xpos: Double, ypos: Double) {

        var alt_xpos: Double = 0.0
        var alt_ypos: Double = 0.0

        cubeCamera.rotateLocal(Math.toRadians((-ypos + yPosition ).toFloat() * 0.02f), Math.toRadians((-xpos + xPosition ).toFloat() * 0.02f),0f )
        camera.rotateLocal(Math.toRadians((-ypos + yPosition ).toFloat() * 0.02f), Math.toRadians((-xpos + xPosition ).toFloat() * 0.02f),0f )
        xPosition = xpos
        yPosition = ypos


        //vorgegeben
        val diff_x = alt_xpos - xpos
        val diff_y = alt_ypos - ypos

        var olpx =0.0
        var oldpy =0.0

        //Kamera um die Spielfigur bewegen
        camera.rotateAroundPoint(0f, diff_x.toFloat() * 0.001f, 0f, cube.getYAxis())
        //camera.rotateAroundPoint(diff_y.toFloat() * 0.001f, 0f, 0f, cat.getXAxis())


        val distanceX = window.mousePos.xpos - olpx
        val distanceY = window.mousePos.ypos - oldpy

        if(distanceX > 0){
            //camera.rotateLocal(0f,Math.toRadians(distanceX.toFloat() *0.02f),0f)
        }
        if(distanceX < 0){
          // camera.rotateLocal(0f,Math.toRadians(distanceX.toFloat() * 0.02f),0f)
        }
        if(distanceY > 0){
           // camera.translateLocal(Vector3f(0f,0.02f,0f))
        }
        if(distanceY < 0){
           // camera.translateLocal(Vector3f(0f,-0.02f,0f))
        }

      olpx = window.mousePos.xpos
        oldpy = window.mousePos.ypos







    }


    fun cleanup() {}

}






