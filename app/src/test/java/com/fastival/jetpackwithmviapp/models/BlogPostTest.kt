package com.fastival.jetpackwithmviapp.models

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BlogPostTest {


    @Test
    fun isBlogPostEqual_identicalProperties_returnTrue(){

        val blogPost =
            BlogPost(1, "post#1", "slug#1", "body#1", "img#1",
                1L, "user#1")

        val blogPost2 =
            BlogPost(1, "post#1", "slug#1", "body#1", "img#1",
                1L, "user#1")

        Assertions.assertEquals(blogPost, blogPost2)

    }

    @Test
    fun isBlogPostEqual_differentPks_returnFalse() {

        val blogPost =
            BlogPost(1, "post#1", "slug#1", "body#1", "img#1",
                1L, "user#1")

        val blogPost2 =
            BlogPost(2, "post#1", "slug#1", "body#1", "img#1",
                1L, "user#1")

        Assertions.assertNotEquals(blogPost, blogPost2)
    }

    @Test
    fun isBlogPostNotEqual_samePksDifferentContents_returnTrue() {

        val blogPost =
            BlogPost(1, "post#1", "slug#1", "body#1", "img#1",
                1L, "user#1")

        val blogPost2 =
            BlogPost(1, "post#2", "slug#1", "body#1", "img#1",
                1L, "user#1")

        Assertions.assertNotEquals(blogPost, blogPost2)

    }

}