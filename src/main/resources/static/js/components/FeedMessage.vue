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
                <v-card-text class="headline font-weight-light" primary-title>
                    {{ tweet.content }}
                </v-card-text>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn @click="delTweet" color="primary" v-if="isMine">Delete</v-btn>
                    <v-btn @click="showDialog = true" v-if="isMine">Update</v-btn>
                </v-card-actions>
            </v-card>
        </v-col>
        <v-dialog max-width="500px" persistent v-model="showDialog">
            <v-card>
                <v-card-title>
                    <span class="headline">Update tweet</span>
                </v-card-title>
                <v-card-text>
                    <v-container>
                        <v-text-field
                                hint="Enter tweet"
                                label="Tweet text"
                                persistent-hint
                                required
                                v-model="tweet.content"/>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-spacer/>
                    <v-btn @click="editTweet" color="blue darken-1" text>Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script>
    export default {
        name: "FeedMessage",
        props: ['tweet', 'deleteTweet', 'showDialog'],
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
                this.showDialog = false;
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