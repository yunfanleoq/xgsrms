<template>
  <div class="md:flex">
    <template v-for="(item, index) in dataList" :key="item.title">
      <ChartCard
        :loading="loading"
        :title="item.title"
        :total="getTotal(item.total, index)"
        class="md:w-1/4 w-full !md:mt-0 !mt-4"
        :class="[index + 1 < 4 && '!md:mr-4']"
      >
        <template #action>
          <a-tooltip title="指标说明">
            <Icon :icon="item.icon" :size="20" />
          </a-tooltip>
        </template>
      </ChartCard>
    </template>
  </div>
</template>
<script lang="ts" setup>
  import { ref, computed } from 'vue';
  import { Icon } from '/@/components/Icon';
  import ChartCard from '/@/components/chart/ChartCard.vue';
  import { chartCardList, bdcCardList } from '../data';

  const props = defineProps({
    loading: {
      type: Boolean,
    },
    type: {
      type: String,
      default: 'chart',
    },
  });
  const dataList = computed(() => (props.type === 'bdc' ? bdcCardList : chartCardList));

  function getTotal(total, index) {
    return index === 0 ? `${total}` : index === 3 ? `${total}` : total;
  }
</script>
