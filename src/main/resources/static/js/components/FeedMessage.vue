<template>
    <v-row align="center" dense>
        <v-col align="center">
            <v-card class="my-lg-4" max-width="500">
                <v-card-title>
                    <v-icon large left>
                        message
                    </v-icon>
                    <span class="title font-weight-light">
                {{ tweet.author.login }}
                ({{ new Date(tweet.created).toLocaleString("ru")}})
            </span>
                </v-card-title>
                <v-card-text class="headline font-weight-bold" primary-title>
                    {{ tweet.content }}
                </v-card-text>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn @click="delTweet" color="primary" v-if="isMine">Удалить</v-btn>
                    <v-btn @click="editTweet" v-if="isMine">Обновить</v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
    </v-row>
</template>

<script>
    export default {
        name: "FeedMessage",
        props: ['tweet', 'deleteTweet'],
        computed: {
            isMine() {
                return this.tweet.author.id == userId
            }
        },
        methods: {
            delTweet() {
                this.deleteTweet(this.tweet)
            },
            editTweet() {
                this.$resource('/tweets').update(this.tweet)
                    .then(response => response.json().then(result => {
                        this.tweet = result;
                        this.$emit('done', result)
                    }))
            }
        }
    }
</script>

<style scoped>

</style>