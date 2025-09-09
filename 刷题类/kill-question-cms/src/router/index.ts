/*
 * @Author: FengXue
 * @Date: 2024-04-08 17:41:43
 * @LastEditors: FengXue
 * @LastEditTime: 2024-05-15 14:02:37
 * @filePath: Do not edit
 */
import { createRouter, createWebHistory, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import Index from '@/views/index.vue' //首页
import Login from '@/views/login.vue' //登录页
import QuestionBank from '@/views/question/questionBank.vue' //问题库
import Question from '@/views/question/question.vue' //问题

const routes: RouteRecordRaw[] = [
    {
        path: '/',
        component: Login,
    },
    {
        path: '/index',
        component: Index,
        children: [
            {
                path: "/question",
                children: [
                    {
                        path: "/question/questionBank",
                        component: QuestionBank
                    },
                    {
                        path: "/question/question:questionBank",
                        name: "question",
                        component: Question
                    },
                ]
            },
        ]
    },
    {
        path: '/login',
        component: Login,
    },

]

const router = createRouter({
    history: createWebHashHistory(),
    routes,
})

export default router;


