<template>
  <div class="file-download-list" v-if="fileList && fileList.length > 0">
    <div class="file-list-header">
      <span class="header-title">附件列表</span>
      <span class="file-count">({{ fileList.length }}个文件)</span>
    </div>
    <div class="file-items">
      <div v-for="(file, index) in fileList" :key="index" class="file-item">
        <div class="file-info">
          <Icon icon="ant-design:paper-clip-outlined" class="file-icon" />
          <span class="file-name" :title="file.fileName">{{ file.fileName || '未命名文件' }}</span>
          <span class="file-size" v-if="file.fileSize">{{ formatFileSize(file.fileSize) }}</span>
        </div>
        <div class="file-actions">
          <a-button type="link" size="small" @click="handleDownload(file)">
            <template #icon><Icon icon="ant-design:download-outlined" /></template>
            下载
          </a-button>
          <a-button type="link" size="small" @click="handlePreview(file)" v-if="canPreview(file)">
            <template #icon><Icon icon="ant-design:eye-outlined" /></template>
            预览
          </a-button>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="no-files">
    <Icon icon="ant-design:file-outlined" class="empty-icon" />
    <span>暂无附件</span>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue';
import { Icon } from '/@/components/Icon';
import { getFileAccessHttpUrl } from '/@/utils/common/compUtils';
import { message } from 'ant-design-vue';

const props = defineProps({
  // 文件数据，可以是字符串（逗号分隔的URL）或数组对象
  value: {
    type: [String, Array],
    default: '',
  },
});

// 解析文件列表
const fileList = computed(() => {
  if (!props.value) return [];
  
  try {
    // 如果是字符串，尝试解析为JSON或按逗号分隔
    if (typeof props.value === 'string') {
      const trimmed = props.value.trim();
      if (!trimmed) return [];
      
      // 尝试解析为JSON数组
      if (trimmed.startsWith('[')) {
        const parsed = JSON.parse(trimmed);
        return Array.isArray(parsed) ? parsed : [];
      }
      
      // 按逗号分隔的文件路径
      return trimmed.split(',').map((path, index) => ({
        filePath: path.trim(),
        fileName: extractFileName(path.trim()),
        fileSize: null,
      }));
    }
    
    // 如果已经是数组
    if (Array.isArray(props.value)) {
      return props.value.map(item => {
        if (typeof item === 'string') {
          return {
            filePath: item,
            fileName: extractFileName(item),
            fileSize: null,
          };
        }
        return {
          ...item,
          fileName: item.fileName || extractFileName(item.filePath || ''),
        };
      });
    }
    
    return [];
  } catch (error) {
    console.error('解析文件列表失败:', error);
    return [];
  }
});

// 从路径中提取文件名
function extractFileName(path: string): string {
  if (!path) return '未命名文件';
  
  // 移除URL参数
  const pathWithoutQuery = path.split('?')[0];
  
  // 提取文件名
  const parts = pathWithoutQuery.split('/');
  const fileName = parts[parts.length - 1];
  
  // 解码URL编码的文件名
  try {
    return decodeURIComponent(fileName);
  } catch {
    return fileName;
  }
}

// 格式化文件大小
function formatFileSize(bytes: number): string {
  if (!bytes || bytes === 0) return '';
  
  const units = ['B', 'KB', 'MB', 'GB'];
  let size = bytes;
  let unitIndex = 0;
  
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }
  
  return `${size.toFixed(2)} ${units[unitIndex]}`;
}

// 判断文件是否可以预览
function canPreview(file: any): boolean {
  const fileName = file.fileName || '';
  const ext = fileName.split('.').pop()?.toLowerCase();
  const previewableExts = ['jpg', 'jpeg', 'png', 'gif', 'pdf', 'txt'];
  return previewableExts.includes(ext || '');
}

// 下载文件
function handleDownload(file: any) {
  try {
    const url = getFileAccessHttpUrl(file.filePath);
    const link = document.createElement('a');
    link.href = url;
    link.download = file.fileName || 'download';
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    message.success('开始下载');
  } catch (error) {
    console.error('下载文件失败:', error);
    message.error('下载文件失败');
  }
}

// 预览文件
function handlePreview(file: any) {
  try {
    const url = getFileAccessHttpUrl(file.filePath);
    window.open(url, '_blank');
  } catch (error) {
    console.error('预览文件失败:', error);
    message.error('预览文件失败');
  }
}
</script>

<style lang="less" scoped>
.file-download-list {
  padding: 16px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
  
  .file-list-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #e8e8e8;
    
    .header-title {
      font-weight: 600;
      font-size: 14px;
      color: #262626;
    }
    
    .file-count {
      margin-left: 8px;
      font-size: 13px;
      color: #8c8c8c;
    }
  }
  
  .file-items {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .file-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 10px 12px;
    background-color: #fff;
    border-radius: 4px;
    border: 1px solid #e8e8e8;
    transition: all 0.3s;
    
    &:hover {
      border-color: #1890ff;
      box-shadow: 0 2px 8px rgba(24, 144, 255, 0.1);
    }
    
    .file-info {
      display: flex;
      align-items: center;
      flex: 1;
      min-width: 0;
      gap: 8px;
      
      .file-icon {
        font-size: 16px;
        color: #1890ff;
        flex-shrink: 0;
      }
      
      .file-name {
        flex: 1;
        font-size: 14px;
        color: #262626;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        min-width: 0;
      }
      
      .file-size {
        font-size: 12px;
        color: #8c8c8c;
        flex-shrink: 0;
      }
    }
    
    .file-actions {
      display: flex;
      gap: 4px;
      flex-shrink: 0;
      margin-left: 16px;
    }
  }
}

.no-files {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px;
  color: #8c8c8c;
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 8px;
    opacity: 0.3;
  }
}
</style>

