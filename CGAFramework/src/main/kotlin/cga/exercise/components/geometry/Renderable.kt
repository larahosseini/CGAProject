package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram

class Renderable(var liste: MutableList<Mesh> ) : Transformable(), IRenderable  {
    override fun render(shaderProgram: ShaderProgram) {

        shaderProgram.setUniform("model_matrix",getWorldModelMatrix(),false)

        for(i in liste){
            i.render(shaderProgram)
        }
    }

}