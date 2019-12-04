<template>
    <v-app>
        <v-content>
            <v-container>
                <Header/>
                <FeedGrid :tweets="tweets"/>
                <Footer/>
            </v-container>
        </v-content>
    </v-app>
</template>

<script>
    import Header from "../components/Header.vue"
    import Footer from "../components/Footer.vue"
    import FeedGrid from "../components/FeedGrid.vue"

    export default {
        name: "App",
        components: {Footer, Header, FeedGrid},

        data: () => ({
            drawer: null,
            tweets: []
        }),

        beforeCreate() {
            if (authenticated) {
                this.$resource('/tweets').get().then(tweetList => {
                    if (tweetList.ok) {
                        tweetList.json().then(parsedResponse => {
                            parsedResponse.forEach(tweet => this.tweets.push(tweet))
                        })
                    }
                })
            }
        }
    }
</script>

<style scoped>
    .main-app {
        color: cornflowerblue;
    }
</style>