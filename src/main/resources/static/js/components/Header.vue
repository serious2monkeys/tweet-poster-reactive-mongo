<template>
    <v-app-bar fixed dense>
        <v-app-bar-nav-icon/>
        <v-toolbar-title>
            Brand-new TweetApp
        </v-toolbar-title>
        <v-spacer/>
        <v-btn v-if="hasUser" href="/logout" icon>
            <v-icon>exit_to_app</v-icon>
        </v-btn>
    </v-app-bar>
</template>

<script>
    export default {
        name: "Header",
        computed:  {
            hasUser() {
                return authenticated;
            }
        }
    }
</script>

<style scoped>

</style>