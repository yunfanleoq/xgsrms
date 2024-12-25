<template>
  <div class="job-detail">
    <main>
      <h2>联系我们</h2>
      <ul class="job-list">
        <li>
          <div class="top-border" v-html="contactText"></div>
        </li>
      </ul>
    </main>
  </div>
</template>
<script setup lang="ts" name="contactUs">
import {onMounted, ref} from 'vue';
import {defHttp} from "@/utils/http/axios";
import { useRouter } from 'vue-router';

const contactList = ref([
  {
    text: "联系我们的相关信息",
  },
])
const contactText = ref(
  "<p>联系我们的相关信息</p>")

const listUrl = "/xgsContactUs/xgsContactUs/list"

const selectList = () => {
  defHttp.get({ url: listUrl, params: {"pageSize" :100} }, { isTransformResponse: false })
    .then((res) => {
      try {
        if (res.success) {
          contactList.value = res.result.records
          contactList.value.reverse()
          if(contactList.value.length > 0){
            contactText.value = contactList.value[0].text
            console.log("contactText=========================", contactText.value)
          }
        }
      } catch (e) {
      }
    })
}

onMounted(selectList)

</script>

<style scoped>

#app {
  display: flex;
  flex-direction: column;
  font-family: Arial, sans-serif;
  background-color: #f4f4f9;
  color: #333;
}

main {
  flex: 1;
  padding: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin: 10px 0;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sidebar li:hover {
  background-color: #f0f0f0;
}

.content h2 {
  margin-bottom: 20px;
  color: #3d54a7;
}

.job-list {
  list-style: none;
  padding-left: 50px;
  padding-right: 50px;
}

.job-list li {
  margin-bottom: 20px;
}

.pagination-button i {
  margin: 0 5px;
}

.top-border {
  border-top: 1px solid #3d54a7;
  padding-bottom: 20px;
  padding-top: 20px;
}

.job-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
}
</style>
